package com.gamecodeschool.snake;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

class SnakeGame extends SurfaceView implements Runnable{

    // Background:
    private Bitmap backgroundBitmap;
    // Objects for the game loop/thread
    private Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;
    // Control how fast the game gets as you play
    private long additionFrameSpeed=0;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;

    // Is the Snake dead?
    private boolean snakeDead=true;

    // for playing sound effects
    private SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;

    // Custom font
    private Typeface customFont;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;

    // How many points does the player have
    private int mScore;
    // What is the current high score
    private int highscore=0;

    // Objects for drawing
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private Paint mPaintNames;

    // A snake ssss
    private Snake mSnake;


    // Create fruit factory
    private FruitFactory mFruitFactory;
    private Fruit mFruit;
    // Used to alternate between apple and banana
    // list of fruits
    private final List<String> fruitTypes = Arrays.asList("Apple", "Grapes", "Banana", "Mango");
    private int currentFruitIndex = 0;


    private Scoreboard mScoreboard;
    private com.gamecodeschool.snake.Credits TestHighScore;
    private com.gamecodeschool.snake.Credits mCreditTig;


    //Current Player
    Player currentPlayer;
    // Get the File for writing scores to
    File pathDirectory;
    String pathDirectoryAsString;
    //How many players have played so far
    private int playerCount=0;


    private long TARGET_FPS;

    private PauseButton mPauseButton;

    // This is the constructor method that gets called
    // from SnakeActivity
    public SnakeGame(Context context, Point size) {
        super(context);

        pathDirectory=context.getFilesDir();
        pathDirectoryAsString= String.valueOf(pathDirectory);
        // Work out how many pixels each block is
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        mNumBlocksHigh = size.y / blockSize;

        // Initialize the SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();


        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the sounds in memory
            descriptor = assetManager.openFd("get_apple.ogg");
            mEat_ID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSP.load(descriptor, 0);

        } catch (IOException e) {
            // Error
        }



        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
        mPaintNames = new Paint();

        mFruitFactory = new ConcreteFruitFactory(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);

        mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        // Load custom font
        customFont = Typeface.createFromAsset(getContext().getAssets(), "Fonts/BungeeSpice-Regular.ttf");
        // Apply font to paint object
        mPaint.setTypeface(customFont);


        // Load the background image
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.game_background);
        // Optionally, scale the bitmap to fit the screen
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, size.x, size.y, false);

        mPauseButton= new PauseButton(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);
        mPauseButton.PauseCreate(NUM_BLOCKS_WIDE,mNumBlocksHigh);
    }



    // Called to start a new game
    public void newGame() {
        // Reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        snakeDead = false;

        // Reset the score and the additional frame speed
        mScore = 0;
        additionFrameSpeed = 0;
        updateGameSpeed();  // Update the game speed based on the reset conditions

        // Spawn the initial fruit
        mFruit = mFruitFactory.createFruit("Apple");
        mFruit.spawn();


        // Reset the frame speed
        additionFrameSpeed=0;

        //Increment the player count
        playerCount++;

        // Setup mNextFrameTime so an update can be triggered
        mNextFrameTime = System.currentTimeMillis();

    }



    // Handles the game loop
    @Override
    public void run() {
        while (mPlaying) {
            if(!mPaused) {
                // Update 10 times a second
                if (updateRequired()) {
                    update();
                }
            }

            draw();
        }
    }


    // Check to see if it is time for an update
//    public boolean updateRequired() {
//
//        // Initially run at 10 frames per second
//        TARGET_FPS = 10+additionFrameSpeed;
//        // There are 1000 milliseconds in a second
//        final long MILLIS_PER_SECOND = 1000;
//
//        // Are we due to update the frame
//        if(mNextFrameTime <= System.currentTimeMillis()){
//            // Tenth of a second has passed
//
//            // Setup when the next update will be triggered
//            mNextFrameTime =System.currentTimeMillis()
//                    + MILLIS_PER_SECOND / TARGET_FPS;
//            //increment the speed of the game
//            additionFrameSpeed=5*mScore;
//            // Return true so that the update and draw
//            // methods are executed
//            return true;
//        }
//
//        return false;
//    }

    public boolean updateRequired() {
        final long MILLIS_PER_SECOND = 1000;

        if (TARGET_FPS <= 0) TARGET_FPS = 10;  // Ensure never dividing by zero
        final long frameTime = MILLIS_PER_SECOND / TARGET_FPS;

        if (mNextFrameTime <= System.currentTimeMillis()) {
            mNextFrameTime = System.currentTimeMillis() + frameTime;
            return true;
        }

        return false;
    }


    private void updateGameSpeed() {
        TARGET_FPS = 10 + additionFrameSpeed;  // Base FPS plus speed increment
        long frameTime = 1000 / TARGET_FPS;
        mNextFrameTime = System.currentTimeMillis() + frameTime;
    }


    private void activateIceCubePowerUp() {
        final long originalFPS = TARGET_FPS;
        TARGET_FPS = Math.max(5, TARGET_FPS / 2);  // Halve the FPS, but not lower than 5

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            TARGET_FPS = originalFPS; // Restore the original FPS after 15 seconds
            mNextFrameTime = System.currentTimeMillis() + 1000 / TARGET_FPS;  // Recalculate next frame time
        }, 15000);
    }






    // Update all the game objects
    public void update() {
        mSnake.move();

        // Check if the snake has eaten the fruit
        if (mSnake.checkDinner(mFruit.getLocation())) {
            // Increase the score for every fruit eaten
            mScore++;

            // Play sound effect for eating a fruit
            mSP.play(mEat_ID, 1, 1, 0, 0, 1);

            // Check the type of the fruit eaten
            if (mFruit instanceof IceCube) {
                // Activate slow down only if the fruit eaten is an IceCube
                activateIceCubePowerUp();
            } else {
                // Increase the game speed for all fruits except IceCube
                additionFrameSpeed += 5;
                updateGameSpeed();  // Call a method to update game speed based on current additionFrameSpeed
            }

            // Decide what fruit to spawn next
            if (new Random().nextInt(10) < 2) {  // Approximately 20% chance to spawn an Ice Cube
                mFruit = mFruitFactory.createFruit("ice-cube");
            } else {
                currentFruitIndex = (currentFruitIndex + 1) % fruitTypes.size();
                mFruit = mFruitFactory.createFruit(fruitTypes.get(currentFruitIndex));
            }
            mFruit.spawn();  // Spawn the selected fruit
        }

        // Check for snake death
        if (mSnake.detectDeath()) {
            mSP.play(mCrashID, 1, 1, 0, 0, 1);
            snakeDead=true;
            mPaused =true;
            if(mScore>highscore){
                highscore=mScore;
            }
            boolean append;
            if (playerCount<=1){
                append = false;
            }
            else{
                append = true;
            }
            // Create the current player
            currentPlayer=new Player("Player"+Integer.toString(playerCount),mScore);

            // attempt to write the player and their score to a file
            try (FileWriter scoreWriter = new FileWriter(pathDirectoryAsString+"/scores.txt", append)) {
                scoreWriter.write(currentPlayer.format());
            } catch (IOException e) {
                throw new RuntimeException(e);

            }
        }
    }



    public void draw() {
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();

            // Clear the canvas, set background, or draw the game's background
            mCanvas.drawBitmap(backgroundBitmap, 0, 0, null);

            // Set the paint for scores
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextSize(60); // Set this size as per your design needs

            // Draw the current score aligned to the right
            String scoreText = "Score: " + mScore;
            float scoreWidth = mPaint.measureText(scoreText);
            mCanvas.drawText(scoreText, mCanvas.getWidth() - scoreWidth - 20, 100, mPaint);

            // Draw the high score right below the current score, also aligned to the right
            String highScoreText = "High Score: " + highscore;
            float highScoreWidth = mPaint.measureText(highScoreText);
            mCanvas.drawText(highScoreText, mCanvas.getWidth() - highScoreWidth - 20, 160, mPaint);

            // Draw other game elements (fruits, snake, etc.)
            if (mFruit != null) {
                mFruit.draw(mCanvas, mPaint);
            }
            mSnake.draw(mCanvas, mPaint);

            // Ensure the pause button is also visible
            if (mPauseButton != null) {
                mPauseButton.draw(mCanvas, mPaint);
            }

            // Game over message, if needed
            if (snakeDead) {
                mCanvas.drawText(getResources().getString(R.string.tap_to_play), 200, 700, mPaint);
            }

            // Unlock and post the draw
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }



    // Do all the drawing
//    public void draw() {
//        // Get a lock on the mCanvas
//        if (mSurfaceHolder.getSurface().isValid()) {
//            mCanvas = mSurfaceHolder.lockCanvas();
//
//            Background backGround = new Background(Color.DKGRAY,Color.DKGRAY,mCanvas);
//            backGround.draw();
//
//            mCanvas.drawBitmap(backgroundBitmap, 0, 0, null);
//
//            // Set the size and color of the mPaint for the text
//            mPaint.setColor(Color.argb(255, 255, 255, 255));
//            mPaint.setTextSize(120);
//
//            mPaintNames.setColor(Color.argb(255, 255, 255, 255));
//            mPaintNames.setTextSize(45);
//
//            mScoreboard = new Scoreboard(mPaint,mScore,mCanvas,20,120);
//            mScoreboard.draw();
//
//            // Draw the High Score in the top right
//            TestHighScore = new Credits(mPaintNames,"High Score:"+Integer.toString(highscore),mCanvas,700,100);
//            TestHighScore.draw();
//
//            mPauseButton.draw(mCanvas,mPaint);
//
//            if (mFruit != null) {  // Check if mFruit is not null
//                mFruit.draw(mCanvas, mPaint);
//            }
//
//            mSnake.draw(mCanvas, mPaint);
//
//            // Draw some text while paused
//            if(snakeDead){
//
//                // Set the size and color of the mPaint for the text
//                mPaint.setColor(Color.argb(255, 255, 255, 255));
//                mPaint.setTextSize(120);
//
//                // Draw the message
//                // We will give this an international upgrade soon
//                //mCanvas.drawText("Tap To Play!", 200, 700, mPaint);
//                mCanvas.drawText(getResources().getString(R.string.tap_to_play), 200, 700, mPaint);
//            }
//
//
//            // Unlock the mCanvas and reveal the graphics for this frame
//            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
//        }
//    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_UP:
                if (mPauseButton.onTouchEvent(motionEvent)){
                    if (mPaused){
                        mPaused=false;
                        resume();
                        break;
                    }
                    else{
                        //pause();
                        mPaused=true;
                    }
                    // Don't want to process snake direction for this tap

                    return true;
                }
                if (mPaused)  {
                    if (snakeDead) {
                        mPaused = false;
                        snakeDead= false;
                        newGame();
                    }


                    // Don't want to process snake direction for this tap
                    return true;
                }

                // Let the Snake class handle the input
                mSnake.switchHeading(motionEvent);
                break;

            default:
                break;

        }
        return true;
    }


    // Stop the thread
    public void pause() {
        mPlaying = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }


    // Start the thread
    public void resume() {
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }
}
