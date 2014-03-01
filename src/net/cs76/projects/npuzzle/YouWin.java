package net.cs76.projects.npuzzle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Activity that displays congratulations on winning puzzle.
 * Contains move amount and full image
 * 
 * @author Greg Chapman
 *
 */
public class YouWin extends Activity
{
   private ImageUtility mImageUtility;
   private String mSelectionPath;
   private int mSelection;

   @Override
   protected void onCreate(Bundle aSavedInstanceState)
   {
      super.onCreate(aSavedInstanceState);

      mImageUtility = new ImageUtility(this);
      setContentView(R.layout.activity_youwin);
     
      int lMoves = 0;
      Intent lIntent = getIntent();
      if(lIntent != null)
      {
         lMoves = lIntent.getExtras().getInt("moves");
         mSelectionPath = lIntent.getExtras().getString("selectionPath");
         mSelection = lIntent.getExtras().getInt("selection");
      }
      
      TextView lMovesText = (TextView) findViewById(R.id.moves);
      lMovesText.setText("You made " + lMoves + " moves");
      
      Button lContinue = (Button) findViewById(R.id.continueBtn);
      lContinue.setOnClickListener(new OnClickListener()
      {
         @Override
         public void onClick(View aView)
         {
            finish();
         }
      });
   }
   
   @Override
   public void onWindowFocusChanged(boolean aHasFocus)
   {
      super.onWindowFocusChanged(aHasFocus);
      mImageUtility.onWindowFocusChanged(aHasFocus);
      
      ImageView lPuzzle = (ImageView) findViewById(R.id.puzzleImage);
      if(!mSelectionPath.isEmpty())
      {
         lPuzzle.setImageBitmap(mImageUtility.scaleImage(mSelectionPath, mSelection));
      }
   }
}
