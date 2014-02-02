package net.cs76.projects.npuzzle;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class YouWin extends Activity
{
   private Point mScreenSize;
   private String mSelectionPath;
   private int mSelection;

   @Override
   protected void onCreate(Bundle aSavedInstanceState)
   {
      super.onCreate(aSavedInstanceState);
      
      mScreenSize = new Point();
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
   
   //TODO make this utility function
   private Bitmap scaleImage()
   {
      Bitmap lOriginal = loadImage();
      if(lOriginal == null)
      {
         return null;
      }
      double lScale = 0;
      double lDestinationWidth = mScreenSize.x;
      double lDestinationHeight = mScreenSize.y;
      if(lOriginal.getWidth() < lOriginal.getHeight())
      {
         lScale = lOriginal.getWidth() / lDestinationWidth;
         int newHeight = (int)Math.ceil(lOriginal.getHeight() / lScale);
         if(newHeight < lDestinationHeight)
         {
            lScale = lOriginal.getHeight() / lDestinationHeight;
            lDestinationWidth = (int)Math.ceil(lOriginal.getWidth() / lScale);
         }
         else
         {
            lDestinationHeight = newHeight;
         }
      }
      else
      {
         lScale = lOriginal.getHeight() / lDestinationHeight;
         int newWidth = (int)Math.ceil(lOriginal.getWidth() / lScale);
         if(newWidth < lDestinationWidth)
         {
            lScale = lOriginal.getWidth() / lDestinationWidth;
            lDestinationHeight = (int)Math.ceil(lOriginal.getHeight() / lScale);
         }
         else
         {
            lDestinationWidth = newWidth;
         }
      }
      Bitmap lReturn = Bitmap.createScaledBitmap
         (lOriginal, (int)lDestinationWidth, (int)lDestinationHeight, false);
      lOriginal.recycle();
      return lReturn;
   }

   private Bitmap loadImage()
   {
      Bitmap lPuzzleImage = null;
      try
      {
         String[] lList = getAssets().list(mSelectionPath);
         if(mSelection < lList.length)
         {
            File lPath = new File(mSelectionPath);
            File lImage = new File(lPath, lList[mSelection]);
            lPuzzleImage = BitmapFactory.decodeStream
                  (getAssets().open(lImage.getPath()), null, null);
         }
      } 
      catch (IOException e)
      {
         e.printStackTrace();
      }
      return lPuzzleImage;
   }
   
   @Override
   protected void onDestroy()
   {
      super.onDestroy();
   }
   
   @Override
   public void onWindowFocusChanged(boolean aHasFocus)
   {
      super.onWindowFocusChanged(aHasFocus);
      View content = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
      mScreenSize.x = content.getWidth();
      mScreenSize.y = content.getHeight();
      
      ImageView lPuzzle = (ImageView) findViewById(R.id.puzzleImage);
      //TODO subset of image
      lPuzzle.setImageBitmap(scaleImage());
   }
   
}
