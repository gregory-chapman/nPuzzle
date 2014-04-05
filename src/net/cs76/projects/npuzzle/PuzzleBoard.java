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

/**
 * Contains the puzzle model logic.
 * Representative puzzle pieces (ids) in puzzle locations.
 * This also loads/saves the game in progress.
 * @author Greg Chapman
 *
 */
public class PuzzleBoard
{
   private GridView mGridView;
   private PuzzleSettings mSettings;
   private ArrayList<Bitmap> mPuzzlePieces;
   private int[] mPuzzleBoard;
   private Point mBlankLocation;
   private Point mMoveLocation;
   private Point[] mMovableLocations;
   private Activity mActivity;
   private int mSelection;
   private String mSelectionPath;
   private boolean mInitializing;

   private enum Locations
   {
      Left, Right, Up, Down
   }

   /**
    * Constructs a puzzle board
    * @param aActivity Activity the board is located on
    * @param aPuzzlePieces Bitmap images of the pieces
    * @param aGridView The grid the puzzle is located on
    * @param aSettings The settings for the puzzle board, includes split
    * @param aSelection The image selection to use for puzzle
    * @param aSelectionPath The path to images in assets
    */
   public PuzzleBoard(Activity aActivity, 
                      ArrayList<Bitmap> aPuzzlePieces, 
                      GridView aGridView, 
                      PuzzleSettings aSettings, 
                      int aSelection, 
                      String aSelectionPath)
   {
      mSelection = aSelection;
      mSelectionPath = aSelectionPath;
      mActivity = aActivity;
      mBlankLocation = new Point();
      mMoveLocation = new Point();
      mPuzzleBoard = new int[aPuzzlePieces.size()];
      mMovableLocations = new Point[4];
      mPuzzlePieces = aPuzzlePieces;
      mGridView = aGridView;
      mSettings = aSettings;
      initializeBoard();
   }

   private void initializeBoard()
   {
      mInitializing = true;
      for (int i = 0; i < mMovableLocations.length; ++i)
      {
         mMovableLocations[i] = new Point(-1, -1);
      }
      for (int i = 0; i < mPuzzleBoard.length; ++i)
      {
         mPuzzleBoard[i] = i;
      }
      updateBlankLocation(mPuzzleBoard.length - 1);
      mInitializing = false;
   }

   private int findPosition(Point aLocation)
   {
      return aLocation.y * mSettings.split + aLocation.x;
   }

   private Point findLocation(int aPosition, Point aLocation)
   {
      aLocation.y = aPosition / mSettings.split;
      aLocation.x = aPosition % mSettings.split;
      return aLocation;
   }

   private void updateBlankLocation(int aPosition)
   {
      if(!mInitializing)
      {
         mSettings.blankLocation = aPosition;
      }
      findLocation(aPosition, mBlankLocation);
      findMovablePieces();
      mGridView.invalidateViews();
      checkPuzzle();
   }

   private void checkPuzzle()
   {
      if(mInitializing) { return; }
      
      boolean lIsComplete = true;
      for(int i = 0; i < mPuzzleBoard.length; ++i)
      {
         if(mPuzzleBoard[i] != i)
         {
            lIsComplete = false;
            break;
         }
      }
      
      if(lIsComplete && mSettings.userMoves != 0)
      {
         launchWinner();
      }
   }
   
   private void launchWinner()
   {
      mSettings.completed = true;
      Intent lIntent = new Intent(mActivity, YouWin.class);
      lIntent.putExtra("moves", mSettings.userMoves);
      lIntent.putExtra("selection", mSelection);
      lIntent.putExtra("selectionPath", mSelectionPath);
      mActivity.startActivity(lIntent);
      mActivity.finish();
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
      if (aX < 0 || aX >= mSettings.split)
      {
         aLocation.x = -1;
      }
      aLocation.y = aY;
      if (aY < 0 || aY >= mSettings.split)
      {
         aLocation.y = -1;
      }
      return aLocation;
   }

   /**
    * Shuffles the board pieces.
    * If this is the first shuffle call after a loaded game, then the shuffle
    * will load the previous locations
    */
   public void shuffle()
   {
      if(!loadShuffle())
      {
         mSettings.userMoves = 0;
         
         //simple reverse
         int lId = mPuzzleBoard.length - 2;
         for (int i = 0; i < (mPuzzleBoard.length - 1); ++i)
         {
            mPuzzleBoard[i] = lId--;
         }
         
         //if split is even, at the end of shuffle, must swap 1,2
         if(mSettings.split % 2 == 0)
         {
            lId = mPuzzleBoard[mPuzzleBoard.length - 2];
            mPuzzleBoard[mPuzzleBoard.length - 2] = mPuzzleBoard[mPuzzleBoard.length - 3];
            mPuzzleBoard[mPuzzleBoard.length - 3] = lId;
         }
         mPuzzleBoard[mPuzzleBoard.length - 1] = mPuzzleBoard.length - 1;
         updateBlankLocation(mPuzzleBoard.length - 1);
      }
   }

   private boolean loadShuffle()
   {
      if(mSettings.loaded)
      {
         mSettings.loaded = false;
         System.arraycopy(mSettings.puzzleLocations, 0, mPuzzleBoard, 0, mPuzzleBoard.length);
         if(valid(mPuzzleBoard))
         {
            updateBlankLocation(mSettings.blankLocation);
            return true;
         }
      }
      return false;
   }

   private boolean valid(int[] aPuzzleBoard)
   {
      for(int piece : aPuzzleBoard)
      {
         if(piece >= mPuzzleBoard.length)
         {
            return false;
         }
      }
      return true;
   }

   /**
    * @return Amount of puzzle pieces
    */
   public int size()
   {
      return mPuzzleBoard.length;
   }

   /**
    * Gets the puzzle piece image for the puzzle location.
    * Locations go left to right, example:
    * 0 1 2
    * 3 4 5
    * 6 7 8
    * @param aPosition Puzzle location
    * @return Puzzle piece image
    */
   public Bitmap getPiece(int aPosition)
   {
      return mPuzzlePieces.get(mPuzzleBoard[aPosition]);
   }

   /**
    * Gets the puzzle piece's original id at the current
    * location index
    * @param aPosition Puzzle location
    * @return Original location of piece
    */
   public long getPieceId(int aPosition)
   {
      return mPuzzleBoard[aPosition];
   }

   /**
    * Will move the puzzle piece if it is movable (blank space adjacent)
    * @param aPosition Current position to move
    */
   public void movePiece(int aPosition)
   {
      if(isMovable(findLocation(aPosition, mMoveLocation)))
      {
         ++mSettings.userMoves;
         int lBlankPosition = findPosition(mBlankLocation);
         int lBlankId = mPuzzleBoard[lBlankPosition];
         mPuzzleBoard[lBlankPosition] = mPuzzleBoard[aPosition];
         mPuzzleBoard[aPosition] = lBlankId;
         updateBlankLocation(aPosition);
         save();
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
   
   /**
    * @return Amount of user movements
    */
   public int getUserMoves()
   {
      return mSettings.userMoves;
   }

   /**
    * Closing the puzzle
    */
   public void dispose()
   {
      save();
   }

   private void save()
   {
      System.arraycopy(mPuzzleBoard, 0, mSettings.puzzleLocations, 0, mSettings.puzzleLocations.length);
      mSettings.save(mActivity);
   }

   /**
    * Implements the touch event for puzzle pieces
    */
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
}
