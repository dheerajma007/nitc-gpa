package com.kkroo.dheeraj.nitcgpa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class FileChooser extends Activity {
    private File currDir, sdCard, extSdcard;
    private ListView listView;
    CustomList arrayAdapter;
    ArrayList<File> fileArrayList = null;
    boolean hasExt = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        this.getActionBar().setIcon(R.drawable.ic_launcher);

        listView = (ListView) findViewById(R.id.listView);
        currDir = null;
        sdCard = new File(System.getenv("EXTERNAL_STORAGE"));
        String secondary = System.getenv("SECONDARY_STORAGE");
        if(secondary != null) {
            extSdcard = new File(secondary);
            if(extSdcard.exists() && extSdcard.canRead())
            hasExt = true;
        }
        fill();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_file_chooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fill()
    {
        fileArrayList = new ArrayList<File>();
        if(currDir != null) {
            final File list[] = currDir.listFiles();
            if(currDir.equals(sdCard))
                this.setTitle("SdCard");
            else if(hasExt && currDir.equals(extSdcard))
                this.setTitle("ExtSdCard");
            else
                this.setTitle(currDir.getName());
            ArrayList<String> nameList = new ArrayList<String>();
            ArrayList<Integer> imgList = new ArrayList<Integer>();

            ArrayList<File> folderList = new ArrayList<File>();
            ArrayList<File> fileList = new ArrayList<File>();

            for (File file : list) {
                if (file.isFile())
                    fileList.add(file);
                else
                    folderList.add(file);
            }

            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
                }
            });
            Collections.sort(folderList, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
                }
            });

            int count = 0;
            File finalList[] = new File[list.length];
            for (int i = 0; i < folderList.size(); i++)
                finalList[count++] = folderList.get(i);
            for (int i = 0; i < fileList.size(); i++)
                finalList[count++] = fileList.get(i);

            if(currDir.equals(sdCard) || (hasExt && currDir.equals(extSdcard)))
                fileArrayList.add(null);
            else
                fileArrayList.add(currDir.getParentFile());
            nameList.add("...");
            imgList.add(R.drawable.ic_prev);


            for (File file : finalList) {
                if(file.isHidden()) continue;
                if (file.isFile()) {
                    if (file.getAbsolutePath().endsWith(".pdf")) {
                        nameList.add(file.getName());
                        imgList.add(R.drawable.ic_file);
                        fileArrayList.add(file);
                    }
                } else {
                    nameList.add(file.getName());
                    imgList.add(R.drawable.ic_file_folder);
                    fileArrayList.add(file);
                }
            }
            String name[] = nameList.toArray(new String[nameList.size()]);
            Integer imgId[] = imgList.toArray(new Integer[imgList.size()]);
            arrayAdapter = new CustomList(this, name, imgId);
        }
        else
        {
            if(hasExt) {
                this.setTitle("/");
                String name[] = {"SdCard", "ExtSdCard"};
                Integer imgId[] = {R.drawable.ic_file_folder, R.drawable.ic_file_folder};
                fileArrayList.add(sdCard);
                fileArrayList.add(extSdcard);
                arrayAdapter = new CustomList(this, name, imgId);
            }
            else
            {
                this.setTitle("/");
                String name[] = {"SdCard"};
                Integer imgId[] = {R.drawable.ic_file_folder};
                fileArrayList.add(sdCard);
                arrayAdapter = new CustomList(this, name, imgId);
            }
        }

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(fileArrayList.get(position) == null || fileArrayList.get(position).isDirectory())
                {
                    currDir = fileArrayList.get(position);
                    fill();
                }
                else
                {
                    Intent resIntent = new Intent();
                    resIntent.putExtra("FilePath", fileArrayList.get(position).getAbsolutePath());
                    setResult(Activity.RESULT_OK, resIntent);
                    finish();
                }
            }
        });
    }
}


