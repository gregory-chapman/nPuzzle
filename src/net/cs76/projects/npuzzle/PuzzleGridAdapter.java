package net.cs76.projects.npuzzle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PuzzleGridAdapter extends BaseAdapter
{
   private Context mContext;
   private Point mScreenSize = new Point();
   private Bitmap mPuzzleImage;
   private int mSplit;
   private ArrayList<Bitmap> mPuzzlePieces;
   private int mScaleWidth;
   private int mScaleHeight;
   
   public PuzzleGridAdapter(Context aContext, Integer aSelection,
                            String aSelectionPath)
   {
      mPuzzlePieces = new ArrayList<Bitmap>();
      mContext = aContext;
      loadImage(aSelection, aSelectionPath);
   }

   /**
    * Will set the split for the puzzle square
    * @param aSplit Split count in one dimension
    * @return True if the image exists and was split
    */
   public boolean setup(int aSplit, Point aScreenSize)
   {
      mSplit = aSplit;
      mScreenSize = aScreenSize;
      
      mPuzzlePieces.clear();
      if(mPuzzleImage == null || mSplit == 0)
      { 
         return false;
      }
      mScaleWidth = mScreenSize.x / mSplit;
      mScaleHeight = mScreenSize.y / mSplit;
      
      Bitmap lScaled = scaleImage();
      
      int lX = 0, lY = 0;
      for(int y = 0; y < mSplit; ++y)
      {
         lX = 0;
         for(int x = 0; x < mSplit; ++x)
         {
            mPuzzlePieces.add(Bitmap.createBitmap
                  (lScaled, lX, lY, mScaleWidth, mScaleHeight));
            lX += mScaleWidth;
         }
         lY += mScaleHeight;
      }
      return true;
   }

   private Bitmap scaleImage()
   {
      double lScale = 0;
      double lDestinationWidth = mScreenSize.x;
      double lDestinationHeight = mScreenSize.y;
      if(mPuzzleImage.getWidth() < mPuzzleImage.getHeight())
      {
         lScale = mPuzzleImage.getWidth() / lDestinationWidth;
         lDestinationHeight = (int)Math.ceil(mPuzzleImage.getHeight() / lScale);
      }
      else
      {
         lScale = mPuzzleImage.getHeight() / lDestinationHeight;
         lDestinationWidth = (int)Math.ceil(mPuzzleImage.getWidth() / lScale);

      }
      return Bitmap.createScaledBitmap
            (mPuzzleImage, (int)lDestinationWidth, (int)lDestinationHeight, false);
   }

   private void loadImage(Integer aSelection, String aSelectionPath)
   {
      try
      {
         String[] lList = mContext.getAssets().list(aSelectionPath);
         if(aSelection < lList.length)
         {
            File lPath = new File(aSelectionPath);
            File lImage = new File(lPath, lList[aSelection]);
            mPuzzleImage = BitmapFactory.decodeStream
                  (mContext.getAssets().open(lImage.getPath()), null, null);
         }
      } 
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Override
   public int getCount()
   {
      return mPuzzlePieces.size();
   }

   @Override
   public Object getItem(int aPosition)
   {
      return mPuzzlePieces.get(aPosition);
   }

   @Override
   public long getItemId(int aPosition)
   {
      return aPosition;
   }

   @Override
   public View getView(int aPosition, View aConvertView, ViewGroup aParent)
   {
      ImageView lImage = (ImageView) aConvertView;
      if(aConvertView == null)
      {
         lImage = new ImageView(mContext);
         lImage.setLayoutParams(new GridView.LayoutParams(mScaleWidth, mScaleHeight));
      }
      lImage.setImageBitmap(mPuzzlePieces.get(aPosition));
      
      return lImage;
   }

}
