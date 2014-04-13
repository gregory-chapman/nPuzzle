package net.cs76.projects.npuzzle;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Settings for persistent puzzle. Save state, load state
 * @author Greg Chapman
 *
 */
public class PuzzleSettings
{
   public boolean loaded;
   public int split;
   public int selection;
   public boolean completed;
   public int[] puzzleLocations;
   public int blankLocation;
   public int userMoves;

   /**
    * Creates the settings object that will be used to save off the game
    * @param aSplit Split of the puzzle
    * @param aSelection Image index of the puzzle
    * @return PuzzleSettings object
    */
   public static PuzzleSettings createSettings(int aSplit, int aSelection)
   {
      PuzzleSettings lSettings = new PuzzleSettings();
      lSettings.loaded = false;
      lSettings.completed = false;
      lSettings.split = aSplit;
      lSettings.selection = aSelection;
      lSettings.puzzleLocations = new int[aSplit*aSplit];
      return lSettings;
   }

   /**
    * Will load the settings from persistent storage
    * @param aActivity 
    * @return Settings from storage or null if complete or none exists
    */
   public static PuzzleSettings load(Activity aActivity)
   {
      PuzzleSettings lSettings = new PuzzleSettings();
      lSettings.loaded = true;
      
      SharedPreferences lPreferences = aActivity.getSharedPreferences("nPuzzle", Activity.MODE_PRIVATE);
      lSettings.completed = lPreferences.getBoolean("completed", true);
      lSettings.blankLocation = lPreferences.getInt("blankLocation", 0);
      lSettings.selection = lPreferences.getInt("selection", 0);
      lSettings.split = lPreferences.getInt("split", 0);
      lSettings.userMoves = lPreferences.getInt("userMoves", 0);
      lSettings.puzzleLocations = splitToIntArray(lPreferences.getString("puzzleLocations", ""));
      
      if(lSettings.completed)
      {
         return null;
      }
      return lSettings;
   }
   
   private static int[] splitToIntArray(String aString)
   {
      String[] lList = aString.split(",");
      int[] lIntList = new int[lList.length];
      int i = 0;
      for(String lValue : lList)
      {
         try
         {
            lIntList[i++] = Integer.decode(lValue);
         }
         catch(Exception aException) { break; }
      }
      return lIntList;
   }

   /**
    * Will save the game to preferences
    * @param aActivity Activity to save preferences for
    */
   public void save(Activity aActivity)
   {
      SharedPreferences lPreferences = aActivity.getSharedPreferences("nPuzzle", Activity.MODE_PRIVATE);
      Editor lEdit = lPreferences.edit();
      lEdit.putBoolean("completed", completed);
      lEdit.putInt("blankLocation", blankLocation);
      lEdit.putInt("selection", selection);
      lEdit.putInt("split", split);
      lEdit.putInt("userMoves", userMoves);
      lEdit.putString("puzzleLocations", combineToString(puzzleLocations));
      lEdit.apply();
      Log.d("nPuzzle", Integer.toString(blankLocation));
   }
   
   private String combineToString(int[] aPuzzleLocations)
   {
      StringBuilder lValue = new StringBuilder();
      for(int i = 0; i < aPuzzleLocations.length; ++i)
      {
         lValue.append(aPuzzleLocations[i]);
         if(i+1 < aPuzzleLocations.length)
         {
            lValue.append(",");
         }
      }
      return lValue.toString();
   }

   public static void reset(Activity aActivity)
   {
      SharedPreferences lPreferences = aActivity.getSharedPreferences
                                          ("nPuzzle", Activity.MODE_PRIVATE);
      Editor lEdit = lPreferences.edit();
      lEdit.putBoolean("completed", true);
      lEdit.apply();
   }
   
   protected PuzzleSettings()
   {
   }

   /**
    * Will get the puzzle board from the settings
    * @param aPuzzleBoard Puzzle board to load the settings to
    */
   public void getPuzzleBoard(int[] aPuzzleBoard)
   {
      System.arraycopy(puzzleLocations, 0, aPuzzleBoard, 0, aPuzzleBoard.length);
   }

   /**
    * Will set the puzzle board
    * @param aPuzzleBoard
    */
   public void setPuzzleBoard(int[] aPuzzleBoard)
   {
      if(puzzleLocations.length != aPuzzleBoard.length)
      {
         puzzleLocations = new int[aPuzzleBoard.length];
      }
      System.arraycopy(aPuzzleBoard, 0, puzzleLocations, 0, puzzleLocations.length);
   }
}
