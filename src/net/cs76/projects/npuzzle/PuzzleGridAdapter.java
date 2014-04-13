package net.cs76.projects.npuzzle;

import java.util.ArrayList;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Adapter for grid view. Serves the puzzle images for the game.
 * Owner class of the puzzle board.
 * @author Greg Chapman
 *
 */
public class PuzzleGridAdapter extends BaseAdapter
{
   private ImageUtility mImageUtility;
   private Activity mActivity;
   private ArrayList<Bitmap> mPuzzlePieces;
   private int mScaleWidth;
   private int mScaleHeight;
   private GridView.LayoutParams mImageLayout;
   private PuzzleBoard mPuzzleBoard;
   private PuzzleSettings mSettings;
   private PuzzleBoard.OnTouchPuzzlePiece mPuzzleOnTouch;
   private GridView mGridView;
   private Integer mSelection;
   private String mSelectionPath;
   
   private static final int sBorderWidth = 1;
   
   /**
    * Serves the image views for the puzzle
    * @param aActivity Activity of the puzzle
    * @param aSelection Selected image index
    * @param aSelectionPath Asset path
    * @param aGrid Grid view containing the puzzle
    * @param aUtility Image utility to serve images
    */
   public PuzzleGridAdapter(Activity aActivity, Integer aSelection,
                            String aSelectionPath, GridView aGrid,
                            ImageUtility aUtility)
   {
      mImageUtility = aUtility;
      mSelection = aSelection;
      mSelectionPath = aSelectionPath;
      mPuzzlePieces = new ArrayList<Bitmap>();
      mActivity = aActivity;
      mGridView = aGrid;
   }

   /**
    * Clears the puzzle and images
    */
   public void dispose()
   {
      for(Bitmap image : mPuzzlePieces)
      {
         image.recycle();
      }
      mPuzzlePieces.clear();
      if(mPuzzleBoard != null)
      {
         mPuzzleBoard.dispose();
      }
      mPuzzleBoard = null;
   }
   
   /**
    * Will set the split for the puzzle square
    * @param aSplit Split count in one dimension
    * @return True if the image exists and was split
    */
   public boolean setup(int aSplit)
   {
      if(aSplit <= 0)
      { 
         return false;
      }
      
      dispose();
      setupPuzzleSettings(aSplit);
      mScaleWidth = mImageUtility.getScreenWidth() / mSettings.split;
      mScaleHeight = mImageUtility.getScreenHeight() / mSettings.split;
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
   
   private void setupPuzzleSettings(int aSplit)
   {
      if(mSettings == null)
      {
         mSettings = PuzzleSettings.createSettings(aSplit, mSelection);
      }
      mSettings.split = aSplit;
   }

   /**
    * Will attempt to load a saved game or returns false
    * @return True if loaded game, false if no game saved
    */
   public boolean setup()
   {
      mSettings = PuzzleSettings.load(mActivity);
      if(mSettings != null)
      {
         return setup(mSettings.split);
      }
      return false;
   }

   private void setupGrid()
   {
      mGridView.setNumColumns(mSettings.split);
   }

   private void initializePuzzleBoard()
   {
      mPuzzleBoard = new PuzzleBoard
                              (mActivity, 
                               mPuzzlePieces, 
                               mGridView, 
                               mSettings,
                               mSelection,
                               mSelectionPath);
      //disable clicking
      mPuzzleOnTouch = null;
      mGridView.invalidateViews();
   }
   
   /**
    * Shuffle game board and allow clicking events
    */
   public void enablePuzzle()
   {
      //enable clicking
      mPuzzleOnTouch = mPuzzleBoard.new OnTouchPuzzlePiece();
      mPuzzleBoard.shuffle();
   }

   private void setupMovablePiece()
   {
      //Replace the last puzzle piece with a special blank piece
      mPuzzlePieces.remove(mPuzzlePieces.size()-1);
      mPuzzlePieces.add(Bitmap.createBitmap(mScaleWidth, mScaleHeight, Bitmap.Config.RGB_565));
   }

   private boolean setupPuzzlePieces()
   {
      Bitmap lScaled = mImageUtility.scaleImage(mSelectionPath, mSelection);
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
      for(int y = 0; y < mSettings.split; ++y)
      {
         lX = 0;
         for(int x = 0; x < mSettings.split; ++x)
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
         lImage = new ImageView(mActivity);
      }
      lImage.setTag(aPosition);
      lImage.setLayoutParams(mImageLayout);
      lImage.setOnTouchListener(mPuzzleOnTouch);
      lImage.setImageBitmap(mPuzzleBoard.getPiece(aPosition));
      
      return lImage;
   }

   /**
    * Resets the board for playing again
    */
   public void reset()
   {
      initializePuzzleBoard();
   }
}
