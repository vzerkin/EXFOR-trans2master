//
//	Program:	x4outfile.java
//	Author:		Viktor Zerkin, V.Zerkin@iaea.org
//	Last modified:	01-Mar-2013
//	Created:	12-Feb-2015
//	Organization:	Nuclear Data Section
//			International Atomic Energy Agency (IAEA)
//			Wagramer Strasse 5, P.O.Box 100, A-1400
//			Vienna, Austria
//	Property of:	International Atomic Energy Agency
//	Project:	Relational Nuclear Reaction Databases
//	Usage:		freely, with proper acknolegement to the IAEA-NDS
//	Distribution:	restricted while the project has not been finished
//	Modifications:	with notification to IAEA-NDS
//	Note:		this is non-commercial software and it comes with
//			NO WARRANTY
//
// 1232A

import java.lang.*;
import java.util.*;
import java.io.*;
import java.sql.*;
import java.util.zip.*;

public class x4outfile
{
    BufferedWriter o3=null;
    PrintWriter o3prn=null;
    String outTxtFileName="x4refdif.txt";
    int nstrOut=0;

    x4outfile(String TxtFileName)
    {
	if (TxtFileName!=null)
	o3=openOutFile(TxtFileName);
	outTxtFileName=TxtFileName;
    }
    x4outfile(String TxtFileName, boolean attach)
    {
	if (TxtFileName!=null)
	o3=openOutFile(TxtFileName,attach);
	outTxtFileName=TxtFileName;
    }

    public PrintWriter getPrintWriter() {
	if (o3==null) return null;
	if (o3prn!=null) return o3prn;
	o3prn=new PrintWriter(o3);
//	o3prn=new PrintWriter(o3,true);
	return o3prn;
    }

    public String pause(String str) {
	System.out.print("\nPause "+str+" ...");
	System.out.flush();
	DataInputStream kbd = new DataInputStream(System.in);
	String temp=null;
	try { temp=kbd.readLine();}
        catch(Exception e) {}
	return(temp);
    }



    public BufferedWriter openOutFile(String TxtFileName)
    {
	BufferedWriter outFile = null;
//	System.out.println("---openOutFile:["+TxtFileName+"]");
	deleteFile(TxtFileName);//??VMS
        try {
	    outFile = new BufferedWriter(new FileWriter(TxtFileName));
	    return outFile;
        }
        catch(IOException ex) {
//	    System.out.println(" ERR-OpenFile...["+TxtFileName+"] "+ex.getMessage());
            return null;
        }
    }
    public BufferedWriter openOutFile(String TxtFileName, boolean attach)
    {
	BufferedWriter outFile = null;
	if (!attach) deleteFile(TxtFileName);//??VMS
        try {
	    outFile = new BufferedWriter(new FileWriter(TxtFileName,attach));
	    return outFile;
        }
        catch(IOException ex) {
            //if (sysOut!=null) sysOut.println(" ERR-OpenFile...["+TxtFileName+"] "+ex.getMessage());
            return null;
        }
    }
    public int close()
    {
	try {o3prn.flush();}
	catch(Exception ex) {}
//	System.out.println("---closeOutFile:["+outTxtFileName+"]");
	if (o3prn!=null)
	try {
	    o3prn.close();
	}
	catch(Exception ex) {}
	o3prn=null;
	if (o3==null) return -2;
	try {
	    o3.close();
	    o3=null;
            return 0;
	}
	catch(IOException ex) {
            return(-1);
        }
    }
    public int write(String str)
    {
	try {
	    o3.write(str);
            return 0;
	}
	catch(IOException ex) {
            return(-1);
        }
    }
    public int writeln(String str)
    {
//	System.out.println("---writeln:"+o3+"["+str+"]");
	try {
	    o3.write(str);
	    o3.newLine();
	    o3.flush();
            return 0;
	}
	catch(IOException ex) {
//	    System.out.println("---problem.writeln:["+str+"]");
            return(-1);
        }
    }


    public void deleteFile(String fileName)
    {
	File f = new File(fileName);
	if (f.exists()) {
	    boolean del=f.delete();
	    //responseOutput.println("\nDelete: "+fileName+" OK:"+del);
	}
    }



}
