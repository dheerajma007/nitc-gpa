package com.kkroo.dheeraj.nitcgpa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.pdmodel.PDDocument;


public class MainActivity extends Activity {

    private static final int ACTIVITY_CHOOSE_FILE = 3;
    public String storageDir = "";
    Dialog calcProgress;
    AlertDialog.Builder dispDlg;
    boolean result = true;
    String errMsg = "", dispStr = "";


    String details,name,roll,branch,dir;
    int count=0,tc=0,ot=0;
    String sem[][];
    int subno[];
    int sc[];
    int fc[];
    int sup[];
    double sgpa[];
    double pcgpa[];
    double cgpa=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getActionBar().setIcon(R.drawable.ic_launcher);
        createFolder();
        setContentView(R.layout.activity_main);
    }

    private void createFolder()
    {
        storageDir = Environment.getExternalStorageDirectory() + "/NitcGpa";
        File mainFolder = new File(storageDir);
        if(!mainFolder.exists())
            mainFolder.mkdir();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickSelect(View v)
    {
        /*
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("application/pdf");
        intent = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
        */

        Intent fileChoose = new Intent(this, FileChooser.class);
        startActivityForResult(fileChoose, 88);
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String path     = "";
        /*if(requestCode == ACTIVITY_CHOOSE_FILE)
        {
            if (resultCode != RESULT_OK) return;
            Uri uri = data.getData();
            String filePath = getRealPathFromURI(uri);
            //Toast.makeText(getApplicationContext(), filePath, Toast.LENGTH_LONG).show();
            if(filePath != null)
            if(filePath.endsWith(".pdf"))
            {
                try {
                    FileInputStream fin = new FileInputStream(new File(filePath));
                    FileOutputStream fout = new FileOutputStream(new File(storageDir + "/gradecard.pdf"));
                    FileChannel inChannel = fin.getChannel();
                    FileChannel outChannel = fout.getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                    fin.close();
                    fout.close();
                } catch (FileNotFoundException fnfex) {
                    Toast.makeText(getApplicationContext(), "Error: " + fnfex.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (IOException ioex) {
                    Toast.makeText(getApplicationContext(), "Error: Cannot read", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getApplicationContext(), "File Copied", Toast.LENGTH_SHORT).show();


                //Toast.makeText(getApplicationContext(), "Processing...", Toast.LENGTH_SHORT).show();
                //processThread process = new processThread();
                //calcProgress  = ProgressDialog.show(this, "Please wait", "Processing", true, false);
                //run();
                //process.start();
                //mainfn();

                new AsyncProcess().execute();

            }
            else
                Toast.makeText(getApplicationContext(), "Choose a .pdf file", Toast.LENGTH_SHORT).show();
        }*/
        if(requestCode == 77)
        {
            if(resultCode != RESULT_OK) return;
            boolean resl = data.getBooleanExtra("Res", false);
            if(resl)
            {
                writeData(storageDir);
            }
        }
        if(requestCode == 88)
        {
            if(resultCode != RESULT_OK) return;
            String filePath = data.getStringExtra("FilePath");
            Toast.makeText(MainActivity.this, filePath, Toast.LENGTH_SHORT).show();

            try {
                FileInputStream fin = new FileInputStream(new File(filePath));
                FileOutputStream fout = new FileOutputStream(new File(storageDir + "/gradecard.pdf"));
                FileChannel inChannel = fin.getChannel();
                FileChannel outChannel = fout.getChannel();
                inChannel.transferTo(0, inChannel.size(), outChannel);
                fin.close();
                fout.close();
            } catch (FileNotFoundException fnfex) {
                Toast.makeText(getApplicationContext(), "Error: " + fnfex.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException ioex) {
                Toast.makeText(getApplicationContext(), "Error: Cannot read", Toast.LENGTH_SHORT).show();
            }

            calcProgress  = ProgressDialog.show(this, "Please wait", "Processing", true, false);
            /*calcProgress = new Dialog(this);
            //calcProgress.setMessage("Processing");
            calcProgress.setTitle("Please wait");
            //calcProgress.setIndeterminate(true);
            calcProgress.setCancelable(false);
            calcProgress.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            calcProgress.getWindow().setBackgroundDrawableResource(R.drawable.dlg_bkgnd);
            calcProgress.setContentView(R.layout.dialog);
            calcProgress.show();*/
            new AsyncProcess().execute();
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String [] proj      = {MediaStore.Images.Media.DATA};
        Cursor cursor       = getContentResolver().query( contentUri, proj, null, null,null);
        if (cursor == null) return null;
        int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void createDir(String name) {
        File file = new File(storageDir + "/" + name);
        if (!file.exists())
            file.mkdir();
    }

    public void writeData(String fPath)
    {
        int i;
        dir=fPath+"/"+name+"_"+roll;
        createDir(name+"_"+roll);
        BufferedWriter det;
        try{
            det = new BufferedWriter(new FileWriter(new File(dir+"/Full_Info.txt")));
            det.write("National Institute of Technology, Calicut"+String.format("%n%n"));
            det.write("NAME: "+name+String.format("%n"));
            det.write("ROLL NO: "+roll+String.format("%n"));
            det.write("BRANCH: "+branch+String.format("%n%n"));
            for(i=0; i<count;i++){
                BufferedWriter out;
                try{
                    out = new BufferedWriter(new FileWriter(new File(dir+"/"+"Semester "+(i+1)+".txt")));
                    out.write("National Institute of Technology, Calicut"+String.format("%n%n"));
                    det.write("SEMESTER : "+(i+1)+String.format("%n"));
                    out.write("SEMESTER : "+(i+1)+String.format("%n%n"));
                    out.write("NAME: "+name+String.format("%n"));
                    out.write("ROLL NO: "+roll+String.format("%n"));
                    out.write("BRANCH: "+branch+String.format("%n%n"));
                    for(int j=0; j<subno[i];j++)
                        out.write(sem[i][j]+String.format("%n"));
                    out.write(String.format("%n")+"SGPA: "+sgpa[i]);
                    out.write(String.format("%n")+"Credits Obtained: "+(sc[i]-fc[i]));
                    if(fc[i]>0)
                        out.write(String.format("%n")+"Credits Lost: "+fc[i]);
                    out.write(String.format("%n%n")+"CGPA: "+pcgpa[i]);
                    out.write(String.format("%n")+"Total Credits: "+tc);
                    det.write("SGPA: "+sgpa[i]);
                    det.write(", Credits: "+(sc[i]-fc[i])+String.format("%n%n"));
                    out.close();
                }catch(IOException e){
                    //System.out.println("\nError Creating File: "+e);
                    //System.exit(0);
                    errMsg = "Error creating file";
                    result = false;
                    return;
                }
            }
            det.write("Current CGPA: "+cgpa+String.format("%n"));
            det.write("Total Credits: "+tc+String.format("%n"));
            det.write("OT Credits: "+ot);
            det.close();
        }catch(IOException e){
            errMsg = "Error creating file";
            result = false;
            return;
        }
        //saveProgress.dismiss();
        Toast.makeText(MainActivity.this, "Saved to folder " + dir, Toast.LENGTH_LONG).show();
        //errMsg = "Saved to folder " + dir;
            /*Notification.Builder notif= new Notification.Builder(MainActivity.this);
            notif.setContentTitle("Export complete");
            notif.setContentText("Saved to folder " + dir);
            notif.setTicker("Saved detailed report");
            //notif.setVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notifMgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notifMgr.notify(1, notif.build());*/

    }

    //*********************************************************
    public class AsyncProcess extends AsyncTask<Void, Void, Boolean> {
    //private class processThread extends Thread{



        //calcProgress  = ProgressDialog.show(this, "Please wait", "Processing", true, false);
        //ProgressDialog calcProgress = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            details = name = roll = branch = dir = "";
            count=tc=ot=0;
            cgpa=0;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(result == false) {
                calcProgress.dismiss();
                Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_LONG).show();
            }
            else
            {
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                intent.putExtra("key", dispStr);
                startActivityForResult(intent, 77);
            }
        }

        public void delete(String name) {
            File file = new File(name);
            if (file.exists())
                try {
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

        public int gp(char g) {
            switch (g) {
                case 'S':
                    return 10;
                case 'A':
                    return 9;
                case 'B':
                    return 8;
                case 'C':
                    return 7;
                case 'D':
                    return 6;
                case 'E':
                    return 5;
                case 'F':
                    return 0;
                case 'P':
                    return 0;
            }
            return 0;
        }

        private String getBranch(String br) {
            if (br.compareTo("EC") == 0)
                return "Electronics and Communication Engineering";
            else if (br.compareTo("EE") == 0)
                return "Electrical and Electronics Engineering";
            else if (br.compareTo("CS") == 0)
                return "Computer Science Engineering";
            else if (br.compareTo("ME") == 0)
                return "Mechanical Engineering";
            else if (br.compareTo("CE") == 0)
                return "Civil Engineering";
            else if (br.compareTo("CH") == 0)
                return "Chemical Engineering";
            else if (br.compareTo("EP") == 0)
                return "Engineering Physics";
            else
                return "Biotechnology";
        }

        private boolean alignData() {
            String line;
            count = 0;
            try {
                BufferedReader in = new BufferedReader(new FileReader(new File(storageDir + "/tmp/clean.txt")));
                BufferedWriter out = new BufferedWriter(new FileWriter(new File(storageDir + "/tmp/aligned.txt")));
                in.readLine();
                while ((line = in.readLine()) != null && line.length() != 0) {
                    if (line.compareTo(details) == 0) {
                        continue;
                    }
                    if ((line.startsWith("Semes"))) {
                        out.write(line + String.format("%n"));
                        count++;
                        continue;
                    }
                    if((line.lastIndexOf("PASS"))>0||(line.lastIndexOf("FAIL"))>0)
                        out.write(line + String.format("%n"));
                    else
                        out.write(line + " ");
                }
                in.close();
                out.close();
                if(count == 0)
                {
                    errMsg = "Wrong input file";
                    result = false;
                    return false;
                }
            } catch (Exception ex) {
                errMsg = ex.getMessage();
                result = false;
                return false;
            }

            return true;
        }

        private boolean clean() {
            String s1 = "Sl No. Code Course Title Credits Grade Result";
            String s2 = "National Institute of Technology, Calicut";
            String s3 = "Page";
            String s4 = "ActiveReports Evaluation. Copyright 2002-2005 (c) Data Dynamics, Ltd. All Rights Reserved.";
            String line, copy = "";
            int c = 0;
            try {
                BufferedReader in = new BufferedReader(new FileReader(new File(storageDir + "/gradeCard.txt")));
                BufferedWriter out = new BufferedWriter(new FileWriter(new File(storageDir + "/tmp/clean.txt")));
                while((line=in.readLine())!=null && line.length()!=0){
                    if((line.compareTo(s2)==0)){
                        c++;
                    }
                    if((line.compareTo(s1)!=0)&&(line.compareTo(s2)!=0)&&(!line.startsWith(s3))&&(line.compareTo(s4)!=0)){
                        if(!line.startsWith("Semes"))
                            out.write(copy+String.format("%n"));
                        copy=line;
                    }
                }
                out.write(s2);
                in.close();
                out.close();

            } catch (FileNotFoundException fnfex) {
                //Toast.makeText(getApplicationContext(), fnfex.getMessage(), Toast.LENGTH_LONG).show();
                errMsg = fnfex.getMessage();
                result = false;
            } catch (IOException ioex) {
                //Toast.makeText(getApplicationContext(), ioex.getMessage(), Toast.LENGTH_LONG).show();
                errMsg = ioex.getMessage();
                result = false;
            }
            if(c==0){
                //Toast.makeText(getApplicationContext(), "Wrong input file", Toast.LENGTH_LONG).show();
                errMsg = "Wrong input file";
                result = false;
            }
            details=copy;
            return true;
        }

        private void convertPdf() {
            try {
                File inFile = new File(storageDir + "/gradecard.pdf");
                /*PDFParser pd = new PDFParser(new FileInputStream(inFile));
                System.out.println("Started parsing");
                pd.parse();
                System.out.println("Finished parsing");
                PDDocument pdf = new PDDocument(pd.getDocument());*/

                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inFile));
                PDDocument pdf = PDDocument.load(bis);

                File file = new File(storageDir + "/gradecard.txt");
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter out = new OutputStreamWriter(fos);
                System.out.println("Started writing");
                out.write(new PDFTextStripper().getText(pdf));
                System.out.println("Finished writing");
                out.close();
                fos.close();
                pdf.close();
            } catch (Exception e) {
                errMsg = e.getMessage();
                result = false;
            }
        }

        private void findDet()
        {
            int p = details.lastIndexOf("B1");
            int inc = details.length();
            name = details.substring(0, p - 1);
            roll = details.substring(p, inc);
            branch = details.substring(inc - 2, inc);
            branch = getBranch(branch);
        }

        private void init()
        {
            sem=new String[count][10];
            subno=new int[count];
            sc=new int[count];
            fc=new int[count];
            sup=new int[count];
            sgpa=new double[count];
            pcgpa=new double[count];
            Arrays.fill(subno, 0);
            Arrays.fill(sc, 0);
            Arrays.fill(fc, 0);
            Arrays.fill(sup, -1);
            Arrays.fill(sgpa, (double) 0);
            Arrays.fill(pcgpa, (double) 0);
            tc=0;ot=0;cgpa=0;

        }

        private void calcData()
        {
            String line;
            int i=1,s=0;

            BufferedReader in;
            try{
                in = new BufferedReader(new FileReader(new File(storageDir + "/tmp/aligned.txt")));
                line=in.readLine();
                while(i<=count){
                    /*switch(line){
                        case "Semester : I": s=0; i++; sup[s]++;
                            break;
                        case "Semester : II": s=1; i++; sup[s]++;
                            break;
                        case "Semester : III": s=2; i++; sup[s]++;
                            break;
                        case "Semester : IV": s=3; i++; sup[s]++;
                            break;
                        case "Semester : V": s=4; i++; sup[s]++;
                            break;
                        case "Semester : VI": s=5; i++; sup[s]++;
                            break;
                        case "Semester : VII": s=6; i++; sup[s]++;
                            break;
                        case "Semester : VIII": s=7; i++; sup[s]++;
                            break;
                    }*/
                    //if(line.startsWith("Semester :"))
                    ///{
                        if (line.equals("Semester : I"))
                        {s = 0; i++; sup[s]++;}
                        if (line.equals("Semester : II"))
                        {s = 1; i++; sup[s]++;}
                        if (line.equals("Semester : III"))
                        {s = 2; i++; sup[s]++;}
                        if (line.equals("Semester : IV"))
                        {s = 3; i++; sup[s]++;}
                        if (line.equals("Semester : V"))
                        {s = 4; i++; sup[s]++;}
                        if (line.equals("Semester : VI"))
                        {s = 5; i++; sup[s]++;}
                        if (line.equals("Semester : VII"))
                        {s = 6; i++; sup[s]++;}
                        if (line.equals("Semester : VIII"))
                        {s = 7; i++; sup[s]++;}


                   // }
                    while((line=in.readLine())!=null && line.length()!=0){
                        if(line.startsWith("Semester"))
                            break;
                        else if(sup[s]>0){
                            line= line.substring(line.indexOf(' ')+1, line.length());
                            String nline=line.substring(0,6);
                            int j=0;
                            while(j<subno[s]){
                                if(nline.compareTo(sem[s][j].substring(0,6))==0){
                                    sem[s][j]=line;
                                    break;

                                }
                                j++;
                            }
                        }
                        else{
                            line= line.substring(line.indexOf(' ')+1, line.length());
                            sem[s][subno[s]]=line;
                            subno[s]++;
                        }
                        int c=0;
                        int p=line.lastIndexOf("PASS");
                        int f=line.lastIndexOf("FAIL");
                        if(p>0){
                            char g=line.charAt(p-2);
                            c=line.charAt(p-4)-48;
                            if(sup[s]>0){
                                sc[s]-=c;
                                fc[s]-=c;
                            }
                            if(g=='P')
                                ot+=c;
                            else{
                                sc[s]+=c;
                                sgpa[s]+=c*gp(g);
                            }
                        }
                        if(f>0){
                            c=line.charAt(f-4)-48;
                            sc[s]+=c;
                            fc[s]+=c;
                        }

                    }
                }
                in.close();
            }catch(IOException e){
                //System.out.println("\nError Opening File: "+e);
                //System.exit(0);
                errMsg = "Error opening file";
                result = false;
                return;
            }
            for(i=count-1; i>0;i--)
                if(sup[i]>0)
                    count--;

        }

        private void dispData()
        {
            int i;
            dispStr = "";
            dispStr = "NAME: "+name;
            dispStr += "\nROLL NO: "+roll;
            dispStr += "\nBRANCH: "+branch+"\n";
            for(i=0; i<count;i++){
                cgpa+=sgpa[i];
                tc+=(sc[i]-fc[i]);
                dispStr += "\nSemester "+(i+1)+": ";
                sgpa[i] = Math.round(sgpa[i]/sc[i] * 100.0) / 100.0;
                dispStr += "\nSGPA: "+sgpa[i];
                dispStr += "\nCredits Obtained: "+(sc[i]-fc[i]);
                pcgpa[i] = Math.round(cgpa/tc * 100.0) / 100.0;

            }
            cgpa = Math.round(cgpa/tc * 100.0) / 100.0;
            dispStr += "\n\nCurrent CGPA: "+cgpa;
            dispStr += "\nTotal Credits: "+tc;
            dispStr += "\nOT Credits: "+ot;

            final String dispStrFin = dispStr;
            calcProgress.dismiss();
            /*dispDlg = new AlertDialog.Builder(MainActivity.this);
            dispDlg.setTitle("CGPA");
            dispDlg.setMessage(dispStr);
            dispDlg.setCancelable(true);
            dispDlg.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //saveProgress = ProgressDialog.show(MainActivity.this, "Please Wait", "Saving...", true, false);
                    writeData(storageDir);
                }
            });
            //dispDlg.create().show();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //dispDlg.create().show();
                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    intent.putExtra("key", dispStrFin);
                    startActivityForResult(intent, 7);


                }
            });
            */

        }






        /*
        private void mainfn() {
            String line, name, roll, branch, dir;
            char g;
            int i = 1, p, f, c, count = 0, tc = 0, sc = 0, inc;
            double sgpa = 0, cgpa = 0, pcgpa = 0;


            //storageDir = Environment.getExternalStorageDirectory() + "/NitcGpa";
            convertPdf();
            createDir("tmp");
            String details = clean();
            p = details.lastIndexOf("B1");
            inc = details.length();
            name = details.substring(0, p - 2);
            roll = details.substring(p, inc);
            branch = details.substring(inc - 2, inc);
            branch = getBranch(branch);
            dir = name + "_" + roll;
            createDir(dir);
            count = alignData(details);
            try {
                BufferedReader in = new BufferedReader(new FileReader(new File(storageDir + "/tmp/aligned.txt")));
            System.out.println("NAME: "+name);
            System.out.println("ROLL NO: "+roll);
            System.out.println("BRANCH: "+branch+"\n");
            //BufferedWriter det = new BufferedWriter(new FileWriter(new File(storageDir + "/" + dir + "/Full_Info.txt")));
            det.write("National Institute of Technology, Calicut" + String.format("%n%n"));
            det.write("NAME: " + name + String.format("%n"));
                det.write("ROLL NO: " + roll + String.format("%n"));
                det.write("BRANCH: " + branch + String.format("%n%n"));
                line = in.readLine();
                while (i <= count) {
                    System.out.println(line);
                    if (line.substring(0, 8).compareTo("Semester") == 0) {
                        line = line.substring(0, 8);
                        line = line.concat(" 0");
                        line = line.replace('0', (char) (i + 48));
                        BufferedWriter out = null;
                        try {
                            out = new BufferedWriter(new FileWriter(new File(storageDir + "/" + dir + "/" + line + ".txt")));
                            out.write("National Institute of Technology, Calicut" + String.format("%n%n"));
                        } catch (IOException e) {
                            System.out.println("\nError Creating File: " + e);
                            System.exit(0);
                        }
                        out.write("NAME: " + name + String.format("%n"));
                        out.write("ROLL NO: " + roll + String.format("%n"));
                        out.write("BRANCH: " + branch + String.format("%n%n"));
                        out.write(line + ": " + String.format("%n%n"));
                        det.write(line + ": ");
                        sc = 0;
                        sgpa = 0;
                        line = in.readLine();
                        p = line.lastIndexOf("PASS");
                        f = line.lastIndexOf("FAIL");
                        while (p > 0) {
                            out.write(line + String.format("%n"));
                            g = line.charAt(p - 2);
                            c = line.charAt(p - 4) - 48;
                            if (g != 'P') {
                                tc += c;
                                sc += c;
                                inc = c * gp(g);
                                sgpa += inc;
                                cgpa += inc;
                            }
                            while (f > 0) {
                                out.write(line + String.format("%n"));
                                sc += c;
                            }
                            line = in.readLine();
                            p = line.lastIndexOf("PASS");
                            f = line.lastIndexOf("FAIL");
                        }
                        sgpa = Math.round(sgpa / sc * 100.0) / 100.0;
                        pcgpa = Math.round(cgpa / tc * 100.0) / 100.0;
                        out.write(String.format("%n") + "SGPA: " + Double.toString(sgpa));
                        out.write(String.format("%n") + "Credits: " + Integer.toString(sc));
                        out.write(String.format("%n%n") + "CGPA: " + Double.toString(pcgpa));
                        out.write(String.format("%n") + "Total Credits: " + Integer.toString(tc));
                        det.write("SGPA: " + Double.toString(sgpa));
                        det.write(", Credits: " + Integer.toString(sc) + String.format("%n%n"));
                        System.out.println("     SGPA: " + sgpa);
                        System.out.println("     Credits: " + sc);
                        out.close();
                        i++;
                    }
                }
                cgpa = Math.round(cgpa / tc * 100.0) / 100.0;
                det.write("CGPA: " + Double.toString(cgpa) + String.format("%n"));
                det.write("Total Credits: " + Integer.toString(tc));
                //System.out.println("\nCGPA: " + cgpa);
                //System.out.println("Total Credits: " + tc);
                //System.out.println("\nCheck the folder '" + dir + "' for more details");
                in.close();
                det.close();
                delete(storageDir + "/tmp/clean.txt");
                delete(storageDir + "/tmp/aligned.txt");
                delete(storageDir + "/tmp/gradeCard.txt");
                delete(storageDir + "/tmp");
                //readinput.nextLine();
                //readinput.close();
            } catch (Exception ex) {

            }
            System.out.println("Exiting...");
            progress.dismiss();
        }*/

        private void getGPA()
        {
            findDet();
            init();
            calcData();
            dispData();
        }

        //@Override
        public void mainFn()
        {
            convertPdf();
            //Toast.makeText(MainActivity.this, "Converted", Toast.LENGTH_LONG).show();
            createDir("tmp");
            if(clean()){
                if(alignData()) {
                    getGPA();
                }
                else {
                    //Toast.makeText(MainActivity.this, "Invalid Grade card", Toast.LENGTH_LONG).show();
                    errMsg = "Invalid Garde card";
                    result = false;
                }

            }else {
                //Toast.makeText(MainActivity.this, "Invalid Grade card", Toast.LENGTH_LONG).show();
                errMsg = "Invalid Garde card";
                result = false;
            }
            //getGPA();
            delete(storageDir + "/tmp/clean.txt");
            delete(storageDir + "/tmp/aligned.txt");
            delete(storageDir + "/tmp/gradeCard.txt");
            delete(storageDir + "/tmp");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mainFn();
            return result;
        }
    }
}
