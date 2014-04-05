package net.cs76.projects.npuzzle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

/**
 * Activity that contains the puzzle board.
 * Will count down and shuffle pieces to start.
 * @author Greg Chapman
 *
 */
public class GamePlay extends Activity
{
   private boolean mInitialized;
   private ImageUtility mImageUtility;
   private PuzzleGridAdapter mAdapter;
   private CountDownTimer mCountDownTimer;
   private static final int sCountdown = 4000;
   private static final int sCountdownInterval = 1000;
   
   @Override
   protected void onCreate(Bundle aSavedInstanceState)
   {
      super.onCreate(aSavedInstanceState);
      
      mInitialized = false;
      setContentView(R.layout.activity_puzzle);
      Bundle lExtras = getIntent().getExtras();
      
      Integer lSelection = lExtras.getInt("selection");
      String lSelectionPath = lExtras.getString("selection.path");
      GridView lGrid = (GridView) findViewById(R.id.puzzleGrid);
      mImageUtility = new ImageUtility(this);
      mAdapter = new PuzzleGridAdapter(this, 
                                       lSelection, 
                                       lSelectionPath, 
                                       lGrid, 
                                       mImageUtility);
      lGrid.setAdapter(mAdapter);
   }
   
   @Override
   protected void onDestroy()
   {
      super.onDestroy();
      mAdapter.dispose();
      if(mCountDownTimer != null)
      {
         mCountDownTimer.cancel();
      }
   }
   
   @Override
   public void onWindowFocusChanged(boolean aHasFocus)
   {
      super.onWindowFocusChanged(aHasFocus);
      mImageUtility.onWindowFocusChanged(aHasFocus);
      
      //only load split if on creation
      if(aHasFocus && !mInitialized)
      {
         initializeGame();
         mInitialized = true;
      }
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem aItem) 
   {
      switch(aItem.getItemId())
      {
         case R.id.settings_reset:
            reset();
            break;
         case R.id.settings_difficulty_easy:
            initializeGame(3);
            break;
         case R.id.settings_difficulty_medium:
            initializeGame(4);
            break;
         case R.id.settings_difficulty_hard:
            initializeGame(5);
            break;
         case R.id.settings_pickpuzzle:
            finish();
            break;
      }
      return true;
   }
   
   private void reset()
   {
      PuzzleSettings.reset(this);
      mAdapter.reset();
      countDownShuffle();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu aMenu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.puzzle, aMenu);
      return true;
   }

   private void initializeGame(int aSplit)
   {
      PuzzleSettings.reset(this);
      if(mAdapter.setup(aSplit))
      {
         countDownShuffle();
      }
   }
   
   private void initializeGame()
   {
      //try to load a previous game
      if(mAdapter.setup())
      {
         mAdapter.enablePuzzle();
      }
      else
      {  //default load
         initializeGame(3);
      }
   }
   
   @SuppressLint("ShowToast")
   private void countDownShuffle()
   {
      final Toast lToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
      //Count down with a toast
      final Handler lDelay = new Handler();
      
      mCountDownTimer = new CountDownTimer(sCountdown, sCountdownInterval)
      {
         @Override
         public void onFinish()
         {
            lToast.cancel();
            mAdapter.enablePuzzle();
         }

         @Override
         public void onTick(long aMillisUntilFinished)
         {
            final int lTimeLeft = (int) aMillisUntilFinished / 1000;
            
            lDelay.post(new Runnable()
            {
               @Override
               public void run()
               {
                  lToast.setText(""+lTimeLeft);
                  lToast.show();
               }
            });
         }
      }.start();
   }
}
