package net.cs76.projects.npuzzle;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * Activity to select a puzzle to play
 * @author Greg Chapman
 *
 */
public class ImageSelection extends ListActivity
{
   private static final String sAssetPath = "puzzles";
   private ImageListAdapter mAdapter;
   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      
      //TODO check if saved game
      
      
      mAdapter = new ImageListAdapter(this, sAssetPath);
      setListAdapter(mAdapter);
   }
   
   @Override
   protected void onListItemClick(ListView aListView, View aView, int aPosition, long aId) 
   {
      Intent lLaunchPuzzle = new Intent(this, GamePlay.class);
      
      lLaunchPuzzle.putExtra("selection", aPosition);
      lLaunchPuzzle.putExtra("selection.path", sAssetPath);
      startActivity(lLaunchPuzzle);
   }

}
