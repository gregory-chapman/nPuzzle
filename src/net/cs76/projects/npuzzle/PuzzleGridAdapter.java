package net.cs76.projects.npuzzle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class PuzzleGridAdapter extends BaseAdapter
{
   private Activity mContext;
   private Point mScreenSize;
   private int mSplit;
   private ArrayList<Bitmap> mPuzzlePieces;
   private int mScaleWidth;
   private int mScaleHeight;
   private GridView.LayoutParams mImageLayout;
   private PuzzleBoard mPuzzleBoard;
   private PuzzleBoard.OnTouchPuzzlePiece mPuzzleOnTouch;
   private GridView mGridView;
   private Integer mSelection;
   private String mSelectionPath;
   private static final int sBorderWidth = 1;
   private static final int sCountdown = 4000;
   private static final int sCountdownInterval = 1000;
   
   public PuzzleGridAdapter(Activity aContext, Integer aSelection,
                            String aSelectionPath, GridView aGrid)
   {
      mSelection = aSelection;
      mSelectionPath = aSelectionPath;
      mScreenSize = new Point();
      mPuzzlePieces = new ArrayList<Bitmap>();
      mContext = aContext;
      mGridView = aGrid;
   }

   public void dispose()
   {
      for(Bitmap image : mPuzzlePieces)
      {
         image.recycle();
      }
      mPuzzlePieces.clear();
      mPuzzleBoard = null;
   }
   
   /**
    * Will set the split for the puzzle square
    * @param aSplit Split count in one dimension
    * @return True if the image exists and was split
    */
   public boolean setup(int aSplit, Point aScreenSize)
   {
      if(aSplit == 0)
      { 
         return false;
      }
      
      dispose();
      mSplit = aSplit;
      mScreenSize = aScreenSize;
      mScaleWidth = mScreenSize.x / mSplit;
      mScaleHeight = mScreenSize.y / mSplit;
      mImageLayout = new GridView.LayoutParams(mScaleWidth, mScaleHeight);
      
      if(setupPuzzlePieces())
      {
         setupMovablePiece();
         setupGrid();
         initializePuzzleBoard();
         return true;
      }
      return false;
   }

   private void setupGrid()
   {
      mGridView.setNumColumns(mSplit);
   }

   private void initializePuzzleBoard()
   {
      mPuzzleBoard = new PuzzleBoard
                           (mContext, 
                            mPuzzlePieces, 
                            mGridView, 
                            mSplit,
                            mSelection,
                            mSelectionPath);
      //disable clicking
      mPuzzleOnTouch = null;
      mGridView.invalidateViews();
      
      countDownShuffle();
   }

   @SuppressLint("ShowToast")
   private void countDownShuffle()
   {
      final Toast lToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
      //Count down with a toast
      final Handler lDelay = new Handler();
      
      new CountDownTimer(sCountdown, sCountdownInterval)
      {
         @Override
         public void onFinish()
         {
            lToast.cancel();
            //enable clicking
            mPuzzleOnTouch = mPuzzleBoard.new OnTouchPuzzlePiece();
            mPuzzleBoard.shuffle();
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

   private void setupMovablePiece()
   {
      //Replace the last puzzle piece with a special blank piece
      mPuzzlePieces.remove(mPuzzlePieces.size()-1);
      mPuzzlePieces.add(Bitmap.createBitmap(mScaleWidth, mScaleHeight, Bitmap.Config.RGB_565));
   }

   private boolean setupPuzzlePieces()
   {
      Bitmap lScaled = scaleImage();
      if(lScaled == null)
      {
         return false;
      }
      
      Canvas lBorder = new Canvas();
      Paint lPaint = new Paint();
      lPaint.setColor(Color.WHITE);
      lPaint.setStrokeWidth(sBorderWidth);
      lPaint.setStyle(Style.STROKE);
      
      int lX = 0, lY = 0;
      for(int y = 0; y < mSplit; ++y)
      {
         lX = 0;
         for(int x = 0; x < mSplit; ++x)
         {
            Bitmap lPiece = Bitmap.createBitmap
                  (lScaled, lX, lY, mScaleWidth, mScaleHeight);
            mPuzzlePieces.add(lPiece);
            
            lBorder.setBitmap(lPiece);
            lBorder.drawRect(0, 0, mScaleWidth-sBorderWidth,
                             mScaleHeight-sBorderWidth, lPaint);
            
            lX += mScaleWidth;
         }
         lY += mScaleHeight;
      }
      lScaled.recycle();
      return true;
   }

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
         String[] lList = mContext.getAssets().list(mSelectionPath);
         if(mSelection < lList.length)
         {
            File lPath = new File(mSelectionPath);
            File lImage = new File(lPath, lList[mSelection]);
            lPuzzleImage = BitmapFactory.decodeStream
                  (mContext.getAssets().open(lImage.getPath()), null, null);
         }
      } 
      catch (IOException e)
      {
         e.printStackTrace();
      }
      return lPuzzleImage;
   }

   @Override
   public int getCount()
   {
      if(mPuzzleBoard == null)
      {
         return 0;
      }
      return mPuzzleBoard.size();
   }

   @Override
   public Object getItem(int aPosition)
   {
      return mPuzzleBoard.getPiece(aPosition);
   }

   @Override
   public long getItemId(int aPosition)
   {
      return mPuzzleBoard.getPieceId(aPosition);
   }

   @Override
   public View getView(int aPosition, View aConvertView, ViewGroup aParent)
   {
      ImageView lImage = (ImageView) aConvertView;
      if(aConvertView == null)
      {
         lImage = new ImageView(mContext);
      }
      lImage.setTag(aPosition);
      lImage.setLayoutParams(mImageLayout);
      lImage.setOnTouchListener(mPuzzleOnTouch);
      lImage.setImageBitmap(mPuzzleBoard.getPiece(aPosition));
      
      return lImage;
   }

   public void reset()
   {
      initializePuzzleBoard();
   }
}
