package net.cs76.projects.npuzzle;

public class PuzzleSettings
{
   public boolean loaded;
   public int split;

   public static PuzzleSettings createSettings(int aSplit)
   {
      PuzzleSettings lSettings = new PuzzleSettings();
      lSettings.loaded = false;
      lSettings.split = aSplit;
      return lSettings;
   }

   public static PuzzleSettings load()
   {
      // TODO Auto-generated method stub
      return null;
   }
   
   public void save()
   {
      
   }
}
