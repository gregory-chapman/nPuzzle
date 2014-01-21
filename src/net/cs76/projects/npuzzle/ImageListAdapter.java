package net.cs76.projects.npuzzle;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageListAdapter extends BaseAdapter
{
   private Bitmap[] mCache;
   private Context mContext;
   private String[] mSelection;
   private String mSelectionPath;
   private static LayoutInflater mInflater = null;

   public ImageListAdapter(Context aContext, String aSelectionPath)
   {
      mSelectionPath = aSelectionPath;
      mContext = aContext;
      mSelection = new String[0];
      try
      {
         mSelection = mContext.getAssets().list(mSelectionPath);
      } 
      catch (IOException e)
      {
         e.printStackTrace();
      }
      mCache = new Bitmap[mSelection.length];
      mInflater = (LayoutInflater) mContext.getSystemService
                              (Context.LAYOUT_INFLATER_SERVICE);
   }

   public int getCount()
   {
      return mSelection.length;
   }

   public Object getItem(int position)
   {
      return position;
   }

   public long getItemId(int position)
   {
      return position;
   }

   public View getView(int position, View convertView, ViewGroup parent)
   {
      View lView = convertView;
      if (convertView == null)
      {
         lView = mInflater.inflate(R.layout.activity_imagelist, null);
      }
      TextView lTitle = (TextView) lView.findViewById(R.id.list_text); // title
      ImageView lThumbImage = (ImageView) lView.findViewById(R.id.list_image); // thumb
                                                                            // image
      // Setting all values in listview
      lTitle.setText(mSelection[position]);
      if(mCache[position] == null)
      {
         try
         {
            File lPath = new File(mSelectionPath);
            File lImage = new File(lPath, mSelection[position]);
            BitmapFactory.Options lOptions = new BitmapFactory.Options();
            lOptions.inSampleSize = 4;
            mCache[position] = BitmapFactory.decodeStream
                  (mContext.getAssets().open(lImage.getPath()), null, lOptions);
         } 
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
      lThumbImage.setImageBitmap(mCache[position]);
      return lView;
   }
}