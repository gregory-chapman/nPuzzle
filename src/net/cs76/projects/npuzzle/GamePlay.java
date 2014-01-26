package net.cs76.projects.npuzzle;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.GridView;

public class GamePlay extends Activity
{
   private PuzzleGridAdapter mAdapter;
   private Point mScreenSize;
   private boolean mCreated = false;
   
   @Override
   protected void onCreate(Bundle aSavedInstanceState)
   {
      super.onCreate(aSavedInstanceState);
      setContentView(R.layout.activity_puzzle);
      Bundle lExtras = getIntent().getExtras();
      
      mCreated = true;
      mScreenSize = new Point();
      Integer lSelection = lExtras.getInt("selection");
      String lSelectionPath = lExtras.getString("selection.path");
      GridView lGrid = (GridView) findViewById(R.id.puzzleGrid);
      mAdapter = new PuzzleGridAdapter(this, lSelection, lSelectionPath, lGrid);
      lGrid.setAdapter(mAdapter);
   }
   
   @Override
   public void onWindowFocusChanged(boolean aHasFocus)
   {
      super.onWindowFocusChanged(aHasFocus);
      View content = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
      mScreenSize.x = content.getWidth();
      mScreenSize.y = content.getHeight();
      
      //only load split if on creation
      if(mCreated)
      {
         setSplit(5);
         mCreated = false;
      }
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem aItem) 
   {
      switch(aItem.getItemId())
      {
         case R.id.settings_reset:
            mAdapter.reset();
            break;
         case R.id.settings_difficulty_easy:
            setSplit(3);
            break;
         case R.id.settings_difficulty_medium:
            setSplit(4);
            break;
         case R.id.settings_difficulty_hard:
            setSplit(5);
            break;
         case R.id.settings_pickpuzzle:
            finish();
            break;
      }
      return true;
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu aMenu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.puzzle, aMenu);
      return true;
   }

   private void setSplit(int aSplit)
   {
      mAdapter.setup(aSplit, mScreenSize);
   }
}
