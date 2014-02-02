package net.cs76.projects.npuzzle;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.GridView;

public class PuzzleBoard
{
   private GridView mGridView;
   private int mSplit;
   private ArrayList<Bitmap> mPuzzlePieces;
   private int[] mPuzzleBoard;
   private Point mBlankLocation;
   private Point mMoveLocation;
   private Point[] mMovableLocations;
   private int mUserMoves;
   private Activity mContext;
   private int mSelection;
   private String mSelectionPath;

   private enum Locations
   {
      Left, Right, Up, Down
   }

   public PuzzleBoard(Activity aContext, 
                      ArrayList<Bitmap> aPuzzlePieces, 
                      GridView aGridView, 
                      int aSplit, 
                      int aSelection, 
                      String aSelectionPath)
   {
      mUserMoves = 0;
      mSelection = aSelection;
      mSelectionPath = aSelectionPath;
      mContext = aContext;
      mBlankLocation = new Point();
      mMoveLocation = new Point();
      mPuzzleBoard = new int[aPuzzlePieces.size()];
      mMovableLocations = new Point[4];
      mPuzzlePieces = aPuzzlePieces;
      mGridView = aGridView;
      mSplit = aSplit;
      initializeBoard();
   }

   private void initializeBoard()
   {
      for (int i = 0; i < mMovableLocations.length; ++i)
      {
         mMovableLocations[i] = new Point(-1, -1);
      }
      for (int i = 0; i < mPuzzleBoard.length; ++i)
      {
         mPuzzleBoard[i] = i;
      }
      updateBlankLocation(mPuzzleBoard.length - 1);
   }

   private int findPosition(Point aLocation)
   {
      return aLocation.y * mSplit + aLocation.x;
   }

   private Point findLocation(int aPosition, Point aLocation)
   {
      aLocation.y = aPosition / mSplit;
      aLocation.x = aPosition % mSplit;
      return aLocation;
   }

   private void updateBlankLocation(int aPosition)
   {
      findLocation(aPosition, mBlankLocation);
      findMovablePieces();
      mGridView.invalidateViews();
      checkPuzzle();
   }

   private void checkPuzzle()
   {
      boolean lIsComplete = true;
      for(int i = 0; i < mPuzzleBoard.length; ++i)
      {
         if(mPuzzleBoard[i] != i)
         {
            lIsComplete = false;
            break;
         }
      }
      
      if(lIsComplete && mUserMoves != 0)
      {
         Intent lIntent = new Intent(mContext, YouWin.class);
         lIntent.putExtra("moves", mUserMoves);
         lIntent.putExtra("selection", mSelection);
         lIntent.putExtra("selectionPath", mSelectionPath);
         mContext.startActivity(lIntent);
         mContext.finish();
      }
   }

   private void findMovablePieces()
   {
      setLocation(mBlankLocation.x - 1, mBlankLocation.y,
            mMovableLocations[Locations.Left.ordinal()]);

      setLocation(mBlankLocation.x + 1, mBlankLocation.y,
            mMovableLocations[Locations.Right.ordinal()]);

      setLocation(mBlankLocation.x, mBlankLocation.y - 1,
            mMovableLocations[Locations.Up.ordinal()]);

      setLocation(mBlankLocation.x, mBlankLocation.y + 1,
            mMovableLocations[Locations.Down.ordinal()]);
   }

   private Point setLocation(int aX, int aY, Point aLocation)
   {
      aLocation.x = aX;
      if (aX < 0 || aX >= mSplit)
      {
         aLocation.x = -1;
      }
      aLocation.y = aY;
      if (aY < 0 || aY >= mSplit)
      {
         aLocation.y = -1;
      }
      return aLocation;
   }

   public void shuffle()
   {
      mUserMoves = 0;
      //simple reverse
      int lId = mPuzzleBoard.length - 2;
      for (int i = 0; i < (mPuzzleBoard.length - 1); ++i)
      {
         mPuzzleBoard[i] = lId--;
      }
      
      //if split is even, at the end of shuffle, must swap 1,2
      if(mSplit % 2 == 0)
      {
         lId = mPuzzleBoard[mPuzzleBoard.length - 2];
         mPuzzleBoard[mPuzzleBoard.length - 2] = mPuzzleBoard[mPuzzleBoard.length - 3];
         mPuzzleBoard[mPuzzleBoard.length - 3] = lId;
      }
      updateBlankLocation(mPuzzleBoard.length - 1);
   }

   public int size()
   {
      return mPuzzleBoard.length;
   }

   public Bitmap getPiece(int aPosition)
   {
      return mPuzzlePieces.get(mPuzzleBoard[aPosition]);
   }

   public long getPieceId(int aPosition)
   {
      return mPuzzleBoard[aPosition];
   }

   public void movePiece(int aPosition)
   {
      if(isMovable(findLocation(aPosition, mMoveLocation)))
      {
         ++mUserMoves;
         int lBlankPosition = findPosition(mBlankLocation);
         int lBlankId = mPuzzleBoard[lBlankPosition];
         mPuzzleBoard[lBlankPosition] = mPuzzleBoard[aPosition];
         mPuzzleBoard[aPosition] = lBlankId;
         updateBlankLocation(aPosition);
      }
   }

   private boolean isMovable(Point aFindLocation)
   {
      for(Point aLocation : mMovableLocations)
      {
         if(aLocation.equals(aFindLocation))
         {
            return true;
         }
      }
      return false;
   }

   public class OnTouchPuzzlePiece implements OnTouchListener
   {
      @Override
      public boolean onTouch(View aView, MotionEvent aEvent)
      {
         if(aEvent.getAction() == MotionEvent.ACTION_DOWN)
         {
            movePiece((Integer)aView.getTag());
         }
         return true;
      }
   }
   
   public int getUserMoves()
   {
      return mUserMoves;
   }
}
