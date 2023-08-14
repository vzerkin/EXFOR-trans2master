//
//	Program:	trans2master.java
//	Author:		Viktor Zerkin, V.Zerkin@iaea.org
//	Last modified:	14-Aug-2023
//	Created:	24-May-2023
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

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.sql.*;
import java.text.*;

public class exfor2subr
{

    boolean extDebug=false;

    boolean flag66c=false;
    int flagOrder=0;
    String fileHead="LIB";

    String inputExforFile="";
    BufferedReader in=null;
    String firstStrFile="";
    String firstStrFileN0="";
    String firstStrFileN1="";
    String firstStrFileN2="";
    String firstStrFileN3="";
    String firstStrFileDateTime="";
    String lastStrFile="";
    String firstStrEntry=null;
    boolean fileStartsWithEntry=false;
    String Entry="",EntryN2="",EntryN3="",EntryN4="",EntryN5="";
    Vector vEntry=new Vector();

/*
    public static void main(String args[])
    {
	String str;
	System.out.println("Hello!!");
	exfor2subr qq=new exfor2subr();
//	qq.setFileDateN2("qq","20230712");
	qq.setFileDateN2("qq","2023-07-11,12:34:54");
//	qq.setFileDateN2("H:\\work2023\\backup2lib\\qq","2023-07-11,12:34:54");
    }
*/

    public Vector vExtractEntry2Subents(String x4inName,String Entry0)
    {
	int ii,ierr=0;
	Vector vec1=new Vector();
	exfor2subr x4file=new exfor2subr();
	ierr=x4file.openInputFile(x4inName);
	if (ierr!=0) return vec1;
	for (ii=0; ;ii++) {
//	for (ii=0; ii<10; ii++) {
	    ierr=x4file.readNextEntry();
	    if (ierr!=0) break;
	    Vector vEntry=x4file.vEntry;
	    String Entry=x4file.Entry;
	    if (!Entry.equals(Entry0)) continue;
	    vec1=getVSubents(vEntry);
	    EntryN2=x4file.EntryN2;
	    break;
	}
	x4file.closeInputFile();
//	System.out.println("---vExtractEntry2Subents---x4inName:["+x4inName+"] Entry=["+Entry0+"] nSub:"+vec1.size()
//	+" EntryN2:"+x4file.EntryN2+" EntryN3:"+x4file.EntryN3+" EntryN4:"+x4file.EntryN4+" EntryN5:"+x4file.EntryN5);
	return vec1;
    }
    public Vector vMergeVSubent(Vector vSubNew,Vector vSubOld)
    {
	int ii,i2,ierr=0;
	String str;
	Vector vAll=new Vector();
//	vAll=vSubNew;
	Vector vec1=getSubNums(vSubNew);
	Vector vec0=getSubNums(vSubOld);
	Hashtable hash=vSub2hash(vSubNew,new Hashtable());
	hash=vSub2hash(vSubOld,hash);
	Vector vec=hash2vector(hash);
	vec=sortVecStr(vec);
//System.out.println("-0-vMergeVSubent::"+vec0.size()+":"+vec0);
//System.out.println("-1-vMergeVSubent::"+vec1.size()+":"+vec1);
//System.out.println("-2-vMergeVSubent::"+vec.size()+":"+vec);
	for (i2=0; i2<vec.size(); i2++) {
	    str=(String)vec.elementAt(i2);
	    Vector vec2=(Vector)hash.get(str);
	    if (vec2==null) continue;
	    if (vec2.size()<=0) continue;
	    vAll.addElement(vec2);
	}

	return vAll;
    }
    public Hashtable vSub2hash(Vector vec1,Hashtable hash)
    {
	int i2;
	String str,Subent;
	for (i2=0; i2<vec1.size(); i2++) {
	    Vector vec2=(Vector)vec1.elementAt(i2);
	    if (vec2.size()<=0) continue;
	    str=(String)vec2.elementAt(0);
	    if (str.length()<22) continue;
	    n1=str.substring(11,22).trim();
	    Vector vv=(Vector)hash.get(n1);
	    if (vv==null) hash.put(n1,vec2);
	}
	return hash;
    }

    public Vector getSubNums(Vector vec1)
    {
	int i2;
	String str,Subent;
	Vector vec=new Vector();
	for (i2=0; i2<vec1.size(); i2++) {
	    Vector vec2=(Vector)vec1.elementAt(i2);
	    if (vec2.size()<=0) continue;
	    str=(String)vec2.elementAt(0);
	    if (str.length()<22) continue;
	    n1=str.substring(11,22).trim();
	    vec.addElement(n1);
	}
	return vec;
    }
    public Vector getVSubents(Vector vec0)
    {
	int i2;
	String str,lastSubent="|||";
	Vector vec1=new Vector();
	Vector vec2=new Vector();
	boolean started=false;
	for (i2=0; i2<vec0.size(); i2++) {
	    str=(String)vec0.elementAt(i2);
//System.out.println(vec1.size()+":"+vec2.size()+"__a__"+i2+"/"+vec0.size()+" Subent:"+isSubentStr(str)+" EndSubent:"+isEndSubentStr(str)+"["+str+"]");
	    if (isNoSubentStr(str)) {
		vec2=new Vector();
		vec2.addElement(str);
		vec1.addElement(vec2);
		started=false;
		//System.out.println(vec1.size()+":"+vec2.size()+"_z_add:\t["+str+"]");
		continue;
	    }
	    if (isEndSubentStr(str)) {
		vec2.addElement(str);
		started=false;
		//System.out.println(vec1.size()+":"+vec2.size()+"_z_add:\t["+str+"]");
		continue;
	    }
	    if (isSubentStr(str)) {
		vec2=new Vector();
		vec2.addElement(str);
		vec1.addElement(vec2);
		started=true;
		//System.out.println(vec1.size()+":"+vec2.size()+"_a_add:\t["+str+"]");
		continue;
	    }
	    if (started) {
		vec2.addElement(str);
		//System.out.println(vec1.size()+":"+vec2.size()+"_b_add:\t["+str+"]");
	    }
	}
	return vec1;
    }
    public int openInputFile(String filename)
    {
	String str1,str2,sss,str;
	int ierr=-1;
	in=null;
	firstStrFile="";
	lastStrFile="";
	firstStrFileN0="";
	firstStrFileN1="";
	firstStrFileN2="";
	firstStrFileN3="";
	firstStrFileDateTime="";
	fileStartsWithEntry=true;
	this.inputExforFile=filename;
	try {
	    in=new BufferedReader(new FileReader(filename));
	    for (;;) {
		sss=in.readLine();
		if (sss==null) break;
		firstStrFile=sss;
		lastStrFile=sss;
		if (firstStrFile.startsWith("ENTRY")) {
//		    firstStrFile="";
		    fileStartsWithEntry=true;
		}
		else {
		    getSubentLine(sss);
		    firstStrFileN0=n0;
		    firstStrFileN1=n1;
		    firstStrFileN2=n2;
		    firstStrFileN3=n3;

		    int itime=str2int(n3,120000);
		    int iformat=0;
		    str=cut66(firstStrFile);
		    if (str.length()<66) iformat=0;
		    else iformat=1;
		    if (iformat==0) itime=str2int(n4,120000);
		    firstStrFileDateTime=n2+"".format("%06d",itime);
		}

		ierr=0;
		break;
	    }
	}
	catch(IOException e) {
//		System.out.println("---ERROR-reading file:[" +filename+"]\n"+e);
//		System.exit(-1);
	}
	return ierr;
    }
    public int closeInputFile()
    {
	int ierr=-1;
	if (in!=null)
	try {in.close();ierr=0;} catch(IOException e) {}
	in=null;
	return ierr;
    }


    public int printEntry(Vector vEntryStr,PrintWriter prn)
    {
	return printEntry(vEntryStr,null,prn);
    }
    public int printEntry(Vector vEntryStr,Vector vSubents,PrintWriter prn)
    {
//	System.out.println("--printEntry--vEntryStr:"+vEntryStr.size());
//	System.out.println("--printEntry--vSubents:"+vSubents.size());
	int i1,i2,nn,ierr=0;
	String str=null,Entry="";
	if ((vEntryStr==null)&&(vSubents==null)) return -1;

	if (vEntryStr!=null)
	if (vEntryStr.size()>0) {
	    str=(String)vEntryStr.elementAt(0);
	    if (isEntryStr(str)) {
		getSubentLine(str);
		Entry=n1;
//if (correctN2date1900)	//20230811
//if (n2.length()==6) n2="19"+n2;
EntryN2=n2;
		nn=1;
		if (flag66c) str=strpad(str,66)+n1+"000"+padstr(""+nn,5);
		prn.println(str);
	    }
	}

	if (vEntryStr==null)
	if (vSubents!=null)
	if (vSubents.size()>0) {
	    Vector vSubent=(Vector)vSubents.elementAt(0);
	    if (vSubent!=null)
	    if (vSubent.size()>0) {
		str=(String)vSubent.elementAt(0);
		getSubentLine(str);
		n1=n1.substring(0,5);
		Entry=n1;
//if (correctN2date1900)	//20230811
//if (n2.length()==6) n2="19"+n2;
EntryN2=n2;
		str=strpad("ENTRY",11)
		+padstr(n1,11)
		+padstr(n2,11)
		+padstr(n3,11)
		+padstr(n4,11)
		+padstr(n5,11)
		;
		nn=1;
		if (flag66c) str=strpad(str,66)+n1+"000"+padstr(""+nn,5);
		try {
		    prn.println(str);
		}
	        catch(Exception ex) {
		    System.out.println("---Exception---"+ex.getMessage());
		    return -3;
		}
	    }
	}

	if (str==null) return -2;

	if (vSubents==null) vSubents=getVSubents(vEntryStr);
//	System.out.println("--printEntry--Subents:"+vSubents.size());
	for (i1=0; i1<vSubents.size(); i1++) {
	    Vector vSubent=(Vector)vSubents.elementAt(i1);
//	    System.out.println("==readNextSubent==lines:"+vSubent.size());
	    for (i2=0; i2<vSubent.size(); i2++) {
		str=(String)vSubent.elementAt(i2);
		if (i2==0) getSubentLine(str);
		nn=i2+1;
		if (i2>0)//20230526:nosubent:1
		if (i2==vSubent.size()-1) nn=99999;
//		System.out.println((i2+1)+"::\t"+str);
		if (flag66c) str=strpad(str,66)+n1+padstr(""+nn,5);
		prn.println(str);
		//if (true) break;
	    }
	}
//?	if (vEntryStr.size()>1) {
	if (vSubents.size()>0) {
	    nn=99999;
	    str=strpad("ENDENTRY",11)+padstr(""+vSubents.size(),11);
	    if (flagOrder>0) str+=padstr("0",11);
	    if (flag66c) str=strpad(str,66)+Entry+"999"+padstr(""+nn,5);
	    prn.println(str);
	}
	return ierr;
    }

    public int readNextEntry()
    {
	vEntry=new Vector();
	String str;
	Vector res=new Vector();
	firstStrEntry=null;
	int ll;
	int i=0;
	Entry="";
	for (;;) {
	    if (fileStartsWithEntry) {
		str=lastStrFile;
		fileStartsWithEntry=false;
	    }
	    else {
		str=readLine(in);
	    }
	    if (str==null) return -1;
//System.out.println("---readNextEntry---str:["+str+"]");
//str=cut66(str);
	    if (!str.trim().equals("")) lastStrFile=str;
	    if (str.startsWith("ENTRY")) {
		str=getSubentLine(str);
		res.addElement(str);
		firstStrEntry=str;
		Entry=n1;
		EntryN2=n2;
		EntryN3=n3;
		EntryN4=n4;
		EntryN5=n5;
		break;
	    }
	    if (str.startsWith("ENDENTRY")) return -2;
	}
	for (;;) {
	    str=readLine(in);
	    if (str==null) return -1;
//System.out.println("---readNextEntry---str:["+str+"] flagOrder:"+flagOrder);
//str=cut66(str);
	    if (!str.trim().equals("")) lastStrFile=str;
	    if (str.startsWith("ENTRY")) return -2;

	    if (str.startsWith("SUBENT")) str=getSubentLine(str);
	    else if (str.startsWith("NOSUBENT")) str=getSubentLine(str);
	    else {
		if (str.length()>66) str=str.substring(0,66);//cut66 here
		str=delEndSpace(str);
//System.out.println("-1-readNextEntry---str:["+str+"] flagOrder:"+flagOrder);
		if (str.startsWith("NOCOMMON")) {//20230711
		    //if (master)
		    if (flagOrder>0)
		    {
			if (str.length()<=11) str=strpad(str,11)+padstr("0",11); //O1608003.x4
			if (str.length()<=22) str=strpad(str,22)+padstr("0",11);
		    }
		}
		else
		if (str.startsWith("END")) {//20230518
		    //if (master)
		    if (flagOrder>0) {
			//"ENDDATA            109" ==> "ENDDATA            109          0"
//20230712		if (str.length()<=22) str=padstr(str,22)+"          0";
			if (str.length()<=22) str=strpad(str,22)+padstr("0",11);
		    }
		    else
		    if (flagOrder<0) {
			//"ENDDATA            109          0" ==> "ENDDATA            109"
			if (str.length()>22) {str=str.substring(0,22);str=delEndSpace(str);}
		    }
		}
	    }

	    res.addElement(str);
	    if (str.startsWith("ENDENTRY")) break;
	}
	vEntry=res;
	return 0;
    }

    boolean isEntryStr(String str)
    {
	if (str==null) return false;
	if (str.startsWith("ENTRY"))   return true;
	if (str.startsWith("NOENTRY")) return true;
	return false;
    }
    boolean isSubentStr(String str)
    {
	if (str==null) return false;
	if (str.startsWith("SUBENT"))   return true;
	if (str.startsWith("NOSUBENT")) return true;
	return false;
    }
    boolean isEndSubentStr(String str)
    {
	if (str==null) return false;
	if (str.startsWith("ENDSUBENT"))return true;
	if (str.startsWith("NOSUBENT")) return true;
	return false;
    }
    boolean isNoSubentStr(String str)
    {
	if (str==null) return false;
	if (str.startsWith("NOSUBENT")) return true;
	return false;
    }

    public String readLine(BufferedReader inFile)
    {
	if (inFile==null) return null;
	String str=null;
	try {
	    str=inFile.readLine();
	}
	catch(IOException ex) {
        }
//?	if (str!=null) str=str.replaceAll("\r","");
//System.out.println("---readLine---str:["+str+"]");
	return str;
    }


    static boolean readingOldFile=true;
    static boolean correctN2date1900=true;
    static boolean correctN3dateD=true;
    String setN3str=null;
//    static boolean readingOldFile=false;
    int iPossibleShift=1;//2023-03-15
    String n0="", n1="", n2="", n3="", n4="", n5="", n50="";
    String altflag="";
    public String getSubentLine(String str00)
    {
	String str;
	n0=""; n1=""; n2=""; n3=""; n4=""; n5=""; n50="";
	altflag="";
//	System.out.println("---getSubentLine---str:["+str00+"]");
	str=strpad(str00,66);
	n0=str.substring(0,10).trim();
	altflag=str.substring(10,11).trim();
	n1=str.substring(11,22).trim();
	n2=str.substring(22,33).trim();
	n3=str.substring(33,44).trim();
	n4=str.substring(44,55).trim();
	n5=str.substring(55,66).trim();
	n50=n5;

	//20230315:corrections for old Master:java -Xmx1400M x4split2x4 exfor-2005-09-14.bck EXFOR-20050914-x4txt
	if (readingOldFile)
	if ((n0.equals("SUBENT"))||(n0.equals("ENTRY"))) {
	    n2=str.substring(22,33+iPossibleShift).trim();
	    n3=str.substring(33+iPossibleShift,44+iPossibleShift).trim();
	    if (n2.length()==7) {
		if (n2.equals("1999012")) n2="19990129";//20230315:SUBENT        C0185001    1999012   19990610
		else n2="1"+n2;				//20230315:SUBENT        40518001    9990902   20000118
	    }
	    if (n1.equals("31429001"))
	    if (n2.equals("070421")) n2="19970421";	//20230315:SUBENT        31429001     070421
	    if (n1.equals("C1202001"))
	    if (n2.equals("2")) n2="20050406";		//20230612:SUBENT        C1202001   2          20051109
	    if (n1.equals("C1202"))
	    if (n2.equals("20000002")) n2="20050406";	//20230612:ENTRY            C1202   20000002   20051109
	}


//	System.out.println(" TransID:["+n5+"] inputExforFile:["+inputExforFile+"]");
//	System.out.println("n1:["+n1+"] n2:["+n2+"] n3:["+n3+"] n4:["+n4+"] n5:["+n5+"]");
	if (correctN3dateD)
	if (n3.equals("D")) n3="";	//20230518
	if (setN3str!=null) n3=setN3str;

	if (correctN2date1900)		//20230531
	if (n2.length()==6) n2="19"+n2;	//20230518

//	if (n4.equals("")) n4=TransDate;
//	if (n5.equals("")) n5=TransID.trim();
//	System.out.println("---firstStrFileNN:["+firstStrFileN0+"]["+firstStrFileN1+"]["+firstStrFileN2+"] inputExforFile:["+inputExforFile+"]");
	if (firstStrFileN0.equals("TRANS")) {
	    if (n4.equals("")) n4=firstStrFileN2;//TransDate
	    if (n5.equals("")) n5=firstStrFileN1;//TransID
	}
	if (n5.equals("1001")) n5="0000";
	if (n5.equals("Y000")) n5="0000";
	if (n5.equals("")) {
	    if (inputExforFile.toUpperCase().startsWith("TRANS.")) {
		n5=inputExforFile.substring(6).trim();
	    }
	}

//	if (n5.equals("")) n5="0000";
	if (n5.length()<4) n5="0000";
//	System.out.println(" TransID:<"+n5+">");
	str=	 strpad(n0,11)
		+padstr(n1,11)
		+padstr(n2,11)
		+padstr(n3,11)
		+padstr(n4,11)
		+padstr(n5,11)
	;
	str=delEndSpace(str);
	return(str);
    }




    public String getHead(String n0,String n1,String n2,String n3,String n4,String n5)
    {
	String str;
	str=strpad(n0,11)
	+padstr(""+n1,11)
	+padstr(""+n2,11)
	+padstr(""+n3,11)
	+padstr(""+n4,11)
	+padstr(""+n5,11)
	;
	if (flag66c) str+="00000000    0";
	//System.out.println("---getHead::str:["+str+"]");
	return str;
    }
    public String getTail(String n0,int nEntries)
    {
	String str;
	str=strpad("END"+n0,11)+padstr(""+nEntries,11);
	if (flagOrder>0) str+=padstr("0",11);
	if (flag66c) str=strpad(str,66)+"Z999999999999";
	System.out.println("---outTail::str:["+str+"]");
	return str;
    }
    public void outHead(String outFileName,String firstStrFile)
    {
	String str;
	x4outfile o1=new x4outfile(outFileName);
	PrintWriter o1prn=o1.getPrintWriter();
	if (!firstStrFile.startsWith("ENTRY")) {
//	    str=firstStrFile.replaceAll("TRANS","LIB  ");
	    getSubentLine(firstStrFile);
            n0=fileHead;
            n5="3";
	    str=strpad(n0,11)
		+padstr(""+n1,11)
		+padstr(""+n2,11)
		+padstr(""+n3,11)
		+padstr(""+n4,11)
		+padstr(""+n5,11)
		;
	    if (flag66c) str+="    0  0    0";
//System.out.println("---outHead::str:["+str+"]");
	    o1prn.println(str);
	}
	if (o1!=null) {o1prn.flush();o1.close();o1=null;}
    }
    public void outTail(String outFileName,String firstStrFile,String lastStrFile,int nEntries)
    {
	String str;
	x4outfile o1=new x4outfile(outFileName);
	PrintWriter o1prn=o1.getPrintWriter();
	if (!firstStrFile.startsWith("ENTRY")) {
	    getSubentLine(firstStrFile);
            n0=fileHead;
	    str=strpad("END"+n0,11)+padstr(""+nEntries,11);
	    if (flag66c) str+=strpad("",44)+"Z999999999999";
//System.out.println("---outTail::str:["+str+"]");
	    o1prn.println(str);
	}
	if (o1!=null) {o1prn.flush();o1.close();o1=null;}
    }
    public void outMakeScript(String scriptFileName,String inFileName,String outFileName
	,String firstStrFileDateTime,String comment)
    {
	String str=firstStrFileDateTime;
	if (str.length()>12) str=str.substring(0,12);
	x4outfile o1=new x4outfile(scriptFileName);
	PrintWriter o1prn=o1.getPrintWriter();
//	o1prn.println("#"+comment);
	o1prn.println("echo "+comment);
	outTailScript(o1prn,fileHead);
	o1prn.println("#Uncomment next line in Windows");
	o1prn.println("#dos2unix -k *.exf");
	o1prn.println("");
	o1prn.println("cat *.exf >"+outFileName);
	o1prn.println("set -x");
	o1prn.println("touch -t "+str+" "+outFileName);
	o1prn.println("cat "+outFileName+" |cut -b-66|sed -e 's/[ \\t]*$//' >"+outFileName+".c66");
	//cat 23601.exf |cut -b-66|sed -e's/[ \t]*$//'>qq1
	o1prn.println("touch -t "+str+" "+outFileName+".c66");
	o1prn.println("ls -lah "+outFileName+"*");
	o1prn.println("set +x");
	if (o1!=null) {o1prn.flush();o1.close();o1=null;}
    }

    public void outTailScript(PrintWriter o1prn,String fileHead)
    {
	o1prn.println("");
	o1prn.println("filenames='*.exf'");
	o1prn.println("ii=0");
	o1prn.println("for name in $filenames; do");
	o1prn.println("    if [ \"$name\" == \"00000head.exf\" ]; then continue ; fi");
	o1prn.println("    if [ \"$name\" == \"zzzzztail.exf\" ]; then continue ; fi");
	o1prn.println("    if [ -f $name ]; then");
	o1prn.println("        ii=$(($ii+1))");
	o1prn.println("    fi");
	o1prn.println("done");
	o1prn.println("echo \"---Total exfor files: $ii\"");
	if (flag66c)
	o1prn.println("printf '%-11s %10d%44s%s\\n' 'END"+fileHead+"' $ii '' 'Z999999999999'>zzzzztail.exf");
	else
	o1prn.println("printf '%-11s %10d\\n' 'END"+fileHead+"' $ii >zzzzztail.exf");
	o1prn.println("");
    }















    public boolean renameFile(String fileOldName, String fileNewName)
    {
	File fold = new File(fileOldName);
	File fnew = new File(fileNewName);
	if (!fold.exists()) return false;
	boolean ok;
	deleteFile(fileNewName);
	//System.out.println("_0_rename:"+fileOldName+":L="+getFileLength(fileOldName)+" "+fileNewName+":L="+getFileLength(fileNewName));
	ok=fold.renameTo(fnew);
	//System.out.println("_1_rename:"+fileOldName+":L="+getFileLength(fileOldName)+" "+fileNewName+":L="+getFileLength(fileNewName));
	return ok;
    }
    public void deleteFile(String fileName)
    {
	//myDelay(1*1000);
	File f = new File(fileName);
	if (f.exists()) {
	    boolean del=f.delete();
//	    sysOut.println("    ...Delete: "+fileName+" OK="+del);
//	    System.out.println("\t...Delete:\t  "+fileName+" OK="+del);
	}
    }
    public long getFileLength(String name)
    {
	long lf;
//name=myStrReplace(name,"\\","/");
	File f = new File(name);
	if (f.exists()) {
	    lf=f.length();
	    return(lf);
	}
	else return(-1);
    }
    static public String getFileDate(String fileName)
    {
	SimpleDateFormat formatter=new SimpleDateFormat ("yyyy-MM-dd,HH:mm:ss");
//	SimpleDateFormat formatter=new SimpleDateFormat ("yyyy-MM-dd");
        File fl;
        java.util.Date fldate=null;
	String strdate="1970-01-01,01:00:00";
	fl=new File(fileName);
	if (fl.exists()) {
	    fldate=new java.util.Date(fl.lastModified());
	    strdate=new String(formatter.format(fldate));
	}
	return strdate;
    }
    public void mkdir(String fileName)
    {
	File f=new File(fileName);
	if (!f.exists()) {
	    boolean del=f.mkdir();
	    System.out.println("    ...mkdir: "+fileName+" OK="+del);
	}
    }
    public int setFileDateN2(String fileName,String strDate)
    {
        File fl;
	String str1;
	int ierr=0;
//	System.out.println("---setFileDateN2---File:["+fileName+"] strDate:["+strDate+"]");
	strDate=strDate.replaceAll("[^0-9]","");
	if (strDate.length()<8) return -1;
//??	if (strDate.length()==8) strDate+="120000";//12==noon
	if (strDate.length()==8) strDate+="060000";//06==6pm

	strDate=strDate.substring(0,4)+"-"+strDate.substring(4,6)+"-"+strDate.substring(6,8)
	+"-"+strDate.substring(8,10)+"-"+strDate.substring(10,12)+"-"+strDate.substring(12,14);

	fl=new File(fileName);
//	java.util.Date fldate=null;
//	fldate=new java.util.Date(fl.lastModified());
//	System.out.println(" File:["+fileName+"] fldate:["+fldate+"]");

	java.util.Date dateDate=null;
	String arr[];

	arr=strDate.split("-");
	if (arr.length==6)
	if (arr[0].length()==4)
	if (arr[1].length()==2)
	if (arr[2].length()==2) {
	    str1=arr[1]+"/"+arr[2]+"/"+arr[0]; //make: 02/18/2004
	    str1+=" "+arr[3]+":"+arr[4]+":"+arr[5];
//	    System.out.println(str1);
	    dateDate=new java.util.Date(str1);
//	    System.out.println(str1+" File:["+fileName+"] date1:["+dateDate+"]");
	    boolean ok=setDateFile(fl,dateDate);
	}

	return ierr;
    }
    public boolean setDateFile(File fl,java.util.Date fldate)
    {
	boolean ok=false;
	if (fl.exists()) {
	    ok=fl.setLastModified(fldate.getTime());
	}
	return ok;
    }




    public Vector hash2vector(Hashtable hValues) {
	Enumeration values;
	Enumeration keys;
	Vector vVal=new Vector();
	String nam;
	Object val;
	int ii;
	values=hValues.elements();
	keys=hValues.keys();
	for (ii=1; keys.hasMoreElements(); ii++) {
	    val=(Object)values.nextElement();
	    nam=(String)keys.nextElement();
	    vVal.addElement(nam);
	}
	return vVal;
    }
    public Vector sortVecStr(Vector vec)
    {
	int i, ii, i0, imin;
	boolean found;
	String str1="", str2="";
	Vector result=vec;
	for (i0=0; i0<result.size(); i0++) {
	    str1=(String)result.elementAt(i0);
	    imin=i0;
	    for (i=i0+1; i<result.size(); i++) {
		str2=(String)result.elementAt(i);
		if (str2.compareTo((String)result.elementAt(imin))<0) {
		    imin=i;
		}
	    }
	    str2=(String)result.elementAt(imin);
	    result.setElementAt(str2,i0);
	    result.setElementAt(str1,imin);
	}
	return result;
    }




    public int str2int(String str, int idefault)
    {
	int i;
	i=idefault;
	try  {i=Integer.parseInt(str);}
	catch(Exception e) {}
	return i;
    }

    public static String cut66(String str) {
	return cut66(str,66);
    }

    public static String cut66(String str, int ll) {
	//str=strpad(str,66);
	if (str.length()>ll) str=str.substring(0,ll);
	str=delEndSpace(str);
	return str;
    }

    public static String strpad(String str, int lpad) {
	String strOut;
	int ii,lstr;
	lstr = str.length();
	if (lstr==lpad) return(str);
	if (lstr>lpad) {
	    strOut=str.substring(0,lpad); //cut
//	    strOut=str;
	    return(strOut);
	}
	strOut=str;
	lstr=lpad-lstr;
	for (ii=0; ii<lstr; ii++) strOut +=" ";
	return(strOut);
    }

    public static String padstr(String str, int lpad) {
	String strOut;
	int ii,lstr;
	lstr = str.length();
	if (lstr==lpad) return(str);
	if (lstr>lpad) {
	    strOut=str.substring(0,lpad); //cut
//	    strOut=str;
	    return(strOut);
	}
	strOut="";
	lstr=lpad-lstr;
	for (ii=0; ii<lstr; ii++) strOut +=" ";
	strOut+=str;
	return(strOut);
    }
    public static String delEndSpace(String str) {
	int ii,lstr;
	lstr=str.length();
	for (ii=lstr-1; ii>=0; ii--)
//	if (str.substring(ii,ii+1).equals(" ")!=true) return(str.substring(0,ii+1));
	if (str.charAt(ii)!=' ') return(str.substring(0,ii+1));
	return "";
    }

}
