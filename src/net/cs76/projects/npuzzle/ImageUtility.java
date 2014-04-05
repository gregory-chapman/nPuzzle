package net.cs76.projects.npuzzle;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.Window;

/**
 * Wraps common image functions for scaling and loading
 * @author Greg Chapman
 */
public class ImageUtility
{
   private Activity mActivity;
   private int mScreenWidth;
   private int mScreenHeight;
   
   /**
    * @param aActivity Activity to serve images to
    */
   public ImageUtility(Activity aActivity)
   {
      mActivity = aActivity;
   }
   
   /**
    * Call when activity window focus changes. 
    * Retrieves the screen content size
    * @param aHasFocus True if focused
    */
   public void onWindowFocusChanged(boolean aHasFocus)
   {
      if(aHasFocus)
      {
         View lContent = mActivity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
         mScreenWidth = lContent.getWidth();
         mScreenHeight = lContent.getHeight();
      }
   }
   
   private Bitmap loadImage(String aSelectionPath, int aSelection)
   {
      Bitmap lPuzzleImage = null;
      try
      {
         String[] lList = mActivity.getAssets().list(aSelectionPath);
         if(aSelection < lList.length)
         {
            File lPath = new File(aSelectionPath);
            File lImage = new File(lPath, lList[aSelection]);
            lPuzzleImage = BitmapFactory.decodeStream
                  (mActivity.getAssets().open(lImage.getPath()), null, null);
         }
      } 
      catch (IOException e)
      {
         e.printStackTrace();
      }
      return lPuzzleImage;
   }
   
   /**
    * Will scale the asset image to the best potential for
    * screen size
    * @param aSelectionPath Path in assets to store image
    * @param aSelection Selected index of image
    * @return Scaled bitmap of the original image
    */
   public Bitmap scaleImage(String aSelectionPath, int aSelection)
   {
      Bitmap lOriginal = loadImage(aSelectionPath, aSelection);
      if(lOriginal == null)
      {
         return null;
      }
      double lScale = 0;
      double lDestinationWidth = mScreenWidth;
      double lDestinationHeight = mScreenHeight;
      if(lOriginal.getWidth() < lOriginal.getHeight())
      {
         lScale = lOriginal.getWidth() / lDestinationWidth;
         int lNewHeight = (int)Math.ceil(lOriginal.getHeight() / lScale);
         if(lNewHeight < lDestinationHeight)
         {
            lScale = lOriginal.getHeight() / lDestinationHeight;
            lDestinationWidth = (int)Math.ceil(lOriginal.getWidth() / lScale);
         }
         else
         {
            lDestinationHeight = lNewHeight;
         }
      }
      else
      {
         lScale = lOriginal.getHeight() / lDestinationHeight;
         int lNewWidth = (int)Math.ceil(lOriginal.getWidth() / lScale);
         if(lNewWidth < lDestinationWidth)
         {
            lScale = lOriginal.getWidth() / lDestinationWidth;
            lDestinationHeight = (int)Math.ceil(lOriginal.getHeight() / lScale);
         }
         else
         {
            lDestinationWidth = lNewWidth;
         }
      }
      Bitmap lReturn = Bitmap.createScaledBitmap
         (lOriginal, (int)lDestinationWidth, (int)lDestinationHeight, false);
      lOriginal.recycle();
      return lReturn;
   }

   /**
    * @return Screen content height
    */
   public int getScreenWidth()
   {
      return mScreenWidth;
   }

   /**
    * @return Screen content height
    */
   public int getScreenHeight()
   {
      return mScreenHeight;
   }
}
