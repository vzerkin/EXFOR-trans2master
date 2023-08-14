//java -Xmx1000M trans2master trans.f094.ord trans.f095x >qq 2>qq2
//java -Xmx1000M trans2master trans.f094.ord trans.f095x -wide>qq
//java -Xmx1000M trans2master trans.f094.ord trans.f095x trans.f096x>qq

//java 13.0.2 2020-01-14 
//java -Xmx2500M -cp x4master.jar trans2master -i:EXFOR-2023-07-18.bck -o:x4new.bck
//java -Xmx2500M -cp x4master.jar trans2master -i:EXFOR-2023-07-18.bck -h:REQUEST,1001,20230515,172831,20230515,3 -o:x4new.bck
//FC EXFOR-2023-07-18.bck x4new.bck 

//
//	Program:	trans2master.java
//	Author:		Viktor Zerkin, V.Zerkin@iaea.org
//	Last modified:	14-Aug-2023
//	Created:	10-May-2023
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

public class trans2master
extends exfor2subr
{

    boolean extDebug=false;

    public static void main(String args[])
    {
	int i,ii,i1,i2,ierr,lnMax=0,nn,ind;
	String str,str1,str2;
        String x4transName=null;
        String x4master=null;

	PrintWriter sysOut=new PrintWriter(System.err,true);

	System.out.println("	+-------------------------------------------+");
	System.out.println("	|  Update EXFOR Master file by TRANS files. |");
	System.out.println("	|    Program trans2master, ver.2023-08-14   |");
	System.out.println("	|      by V.Zerkin, IAEA, Vienna, 2023      |");
	System.out.println("	+-------------------------------------------+");
	if (args.length<1) {
	    System.out.println("");
	    System.out.println(" Purpose.");
	    System.out.println("   To be used for standalone maintenance of EXFOR Master file");
	    System.out.println("   implementing procedure of EXFOR Master file updates:");
	    System.out.println("   - read EXFOR file (Backup/Master or any other) into buffer;");
	    System.out.println("   - read TRANS file(s) and updates Entries in the buffer;");
	    System.out.println("   - save content of buffer to new EXFOR Master file.");
	    System.out.println("");
	    System.out.println(" Run:");
	    System.out.println("   $ java [flags] trans2master [-option] {[-option]} ...");
	    System.out.println("");
	    System.out.println(" Options:");
	    System.out.println("    settings applied to input files:");
	    System.out.println("   -add19	   add 19 (when missing) to N2 for ENTRY/SUBENT");
	    System.out.println("   -n3set:N3       set N3 in ENTRY/SUBENT: date of update");
	    System.out.println("   -n3clean        correct N3 removing extra symbols");
            System.out.println("    input files:");
	    System.out.println("   -i:filename     read initial EXFOR Master file into buffer");
	    System.out.println("   -t:filename     read EXFOR TRANS file, update buffer");
	    System.out.println("    settings for output:");
	    System.out.println("   -order          add N3=0 to END* lines, to be compatible with ORDER");
	    System.out.println("   -wide           add to EXFOR file right column (default: cut after 66-col.)");
	    System.out.println("   -h:N0,N1,..,N5  set Header-line in output file");
	    System.out.println("    output:");
	    System.out.println("   -o:filename     write buffer to new EXFOR Master file");
	    System.out.println("   -od:dir         write buffer to dir/1/123/ by one file for ENTRY");
	    System.out.println("    input-output without buffer:");
	    System.out.println("   -split:file:dir split EXFOR file by Entries and save in dir/1/123/12345.x4");
	    System.out.println("");
	    System.out.println(" Examples:");
	    System.out.println("  1) load Master file, update by Trans-file(s), write new Master file");
	    System.out.println("   $ java -Xmx4000M -cp x4master.jar trans2master \\");
	    System.out.println("     -i:EXFOR-2023-04-29.bck                      \\");
	    System.out.println("     -n3set:20230515 -t:trans.c222                \\");
	    System.out.println("     -h:REQUEST,1001,20230515,172831,20230515,3   \\");
	    System.out.println("     -o:EXFOR-2023-05-15.new");
	    System.out.println("");
	    System.out.println("  2) merge EXFOR files, write to new EXFOR file");
	    System.out.println("   $ java -cp x4master.jar trans2master -t:trans.3208 -t:trans.e137 -o:new.x4");
	    System.out.println("");
	    System.out.println("  3) merge EXFOR files, save Entry files to output sub-directories");
	    System.out.println("   $ java -cp x4master.jar trans2master -t:trans.3208 -t:trans.e137 -wide -od:X4all/");
	    System.out.println("");
	    System.out.println("  4) split EXFOR file into sub-directories");
//	    System.out.println("   $ java -cp x4master.jar trans2master -i:EXFOR-2023-05-23.bck -od:x4all");
	    System.out.println("   $ java -cp x4master.jar trans2master -split:trans.3208:x4all");
	    System.out.println("");
    	System.exit(0);
        }


	i=0;

	long msec0,msec1=System.currentTimeMillis();
	msec0=System.currentTimeMillis();

	trans2master x4file=new trans2master();
//	System.out.println("---trans2master---x4master:["+x4master+"]");

        for (; i<args.length; i++) {
            str=args[i];
//	    if (str.startsWith("-"))
	    System.out.println("  args["+i+"]=["+str+"]");
	    if (str.equals("-wide")) {
		System.out.println("\t#Option: "+str);
		x4file.flag66c=true;
		continue;
	    }
	    if (str.equals("-order")) {
		System.out.println("\t#Option: "+str);
		x4file.flagOrder=1;
		continue;
	    }
	    if (str.equals("-add19")) {
		System.out.println("\t#Option: "+str);
		x4file.correctN2date1900=true;
		continue;
	    }
	    if (str.equals("-n3clean")) {
		System.out.println("\t#Option: "+str);
		x4file.correctN3dateD=true;
		continue;
	    }
	    if (str.startsWith("-n3set:")) {
		System.out.println("\t#Option: "+str);
		str1=str.substring(7).trim();
		if (str1.length()==8) x4file.setN3str=str1;
		else x4file.setN3str=null;
		continue;
	    }
	    if (str.startsWith("-h:")) {
		System.out.println("\t#Option: "+str);
		str1=str.substring(3).trim();
		x4file.setHeader(str1);
		continue;
	    }
	    if (str.startsWith("-o:")) {
		System.out.println("\t#Option: "+str);
		str1=str.substring(3).trim();
		x4file.setN3str=null;
		if (!str1.equals("")) x4file.outMasterFile(str1);
		continue;
	    }
	    if (str.startsWith("-od:")) {
		System.out.println("\t#Option:: "+str);
		str1=str.substring(4).trim();
		x4file.setN3str=null;
		if (!str1.equals("")) x4file.outMasterFileByEntries(str1);
		continue;
	    }
	    if (str.startsWith("-split:")) {
		System.out.println("\t#Option:: "+str);
		str1=str.substring(7).trim();
		if (!str1.equals("")) {
		    ind=str1.indexOf(":");
		    str2="";
		    if (ind>0) {
			str2=str1.substring(ind+1);
			str1=str1.substring(0,ind);
		    }
		    System.out.println("---splitExforFile2dir---x4name:["+str1+"]");
		    x4file.splitExforFile2dir(str1,str2);
		}
		continue;
	    }
	    if (str.startsWith("-i:")) {
		System.out.println("\t#Option: "+str);
		str1=str.substring(3).trim();
		if (!str1.equals("")) {
		    x4master=str1;
		    x4file.readMasterFile(x4master);
		}
		continue;
	    }
	    if (str.startsWith("-t:")) {
		System.out.println("\t#Option: "+str);
		str1=str.substring(3).trim();
		if (!str1.equals("")) {
		    x4transName=str1;
		    System.out.println("---trans2master---x4transName:["+x4transName+"]");
		    x4file.applyTransFile(x4transName);
		}
		continue;
	    }
	}

	msec1=System.currentTimeMillis();
	System.out.println("\n===trans2master===time:"+((msec1-msec0)/1000.)+"sec");

    }

    public trans2master()
    {
    }

    String fileHead="REQUEST";
    String n1head="1001",n2head="",n3head="",n4head="",n5head="3";

    boolean flag66c=false;
    int flagOrder=-1;
    boolean correctN2date1900=false;
    String setN3str=null;
    boolean correctN3dateD=false;

    exfor2subr x4MasterFile=new exfor2subr();
    Hashtable hMasterEntry=new Hashtable();
    exfor2subr x4LastFile=new exfor2subr();
    String outFileName=null;
    String x4outDir="x4all-1/";

    public void setHeader(String str0)
    {
	int ii;
	fileHead=str0;
	String arr[];
	arr=str0.split(",");
	for (ii=0; ii<arr.length; ii++) {
	    System.out.println(ii+") ["+arr[ii]+"]");
	    if (ii==0) fileHead=arr[ii];
	    if (ii==1) n1head=arr[ii];
	    if (ii==2) n2head=arr[ii];
	    if (ii==3) n3head=arr[ii];
	    if (ii==4) n4head=arr[ii];
	    if (ii==5) n5head=arr[ii];
	}
    }
    public int readMasterFile(String x4inName)
    {
	int nEntries=0,i,ii,isub,ierr,lnMax=0;
	String str;
	//hMasterEntry=new Hashtable();
	System.out.println("---readMasterFile----input file:["+x4inName+"]");

	long msec0,msec1=System.currentTimeMillis();
	msec0=System.currentTimeMillis();
	exfor2subr x4file=new exfor2subr();
	x4file.flag66c=flag66c;
	x4file.flagOrder=flagOrder;
	x4file.fileHead=fileHead;
	x4file.setN3str=setN3str;
	x4file.correctN2date1900=correctN2date1900;
	x4file.correctN3dateD=correctN3dateD;
	ierr=x4file.openInputFile(x4inName);
	if (ierr!=0) {
	    System.out.println("Error opening:["+x4inName+"]");
	    System.exit(1);
	}
	System.out.println("---openInputFile---firstStrFile=["+x4file.firstStrFile+"]");
	x4MasterFile=x4file;
	x4LastFile=x4file;

	for (ii=0; ;ii++) {
//	for (ii=0; ii<10; ii++) {
	    ierr=x4file.readNextEntry();
	    msec1=System.currentTimeMillis();
	    if (ierr!=0) break;
	    Vector vEntry=x4file.vEntry;
	    String Entry=x4file.Entry;
	    if (vEntry.size()>lnMax) lnMax=vEntry.size();
	    Vector vSubent=getVSubents(vEntry);
	    System.out.print("#"+(ii+1)+")\t--readNextEntry--"
		+"["+x4file.firstStrEntry+"]"
		+" #ln:"+vEntry.size()
		+" #Sub:"+vSubent.size()
		+" t:"+((msec1-msec0)/1000.)+"s"
		+"     \r"
		);
	    hMasterEntry.put(Entry,vSubent);
	    nEntries++;
	}
	x4file.closeInputFile();
	System.out.println("");
	System.out.println("---readMasterFile---fileName----:["+x4inName+"]");
	System.out.println("---readMasterFile---firstStrFile=["+x4file.firstStrFile+"]");
	System.out.println("---readMasterFile---lastStrFile==["+x4file.lastStrFile+"]");
	msec1=System.currentTimeMillis();
	System.out.println("---readMasterFile::time:"+((msec1-msec0)/1000.)+"sec"+" lnMax:"+lnMax);

	return nEntries;
    }

    public void init_exfor2subr(exfor2subr x4file)
    {
	x4file.flag66c=flag66c;
	x4file.flagOrder=flagOrder;
	x4file.fileHead=fileHead;
	x4file.setN3str=setN3str;
	x4file.correctN2date1900=correctN2date1900;
	x4file.correctN3dateD=correctN3dateD;
    }
    public int applyTransFile(String x4transName)
    {
	int nEntries=0,i,ii,i2,isub,ierr,lnMax=0;
	String str;
	System.out.println("\n---applyTransFile---input file:["+x4transName+"]");

	long msec0,msec1=System.currentTimeMillis();
	msec0=System.currentTimeMillis();
	exfor2subr x4file=new exfor2subr();
	init_exfor2subr(x4file);

	ierr=x4file.openInputFile(x4transName);
	if (ierr!=0) {
	    System.out.println("Error opening:["+x4transName+"]");
	    System.exit(1);
	}
	System.out.println("---openInputFile---firstStrFile=["+x4file.firstStrFile+"]");
	x4LastFile=x4file;
//	outFileName=x4outDir+"00000head.exf";
//	x4file.outHead(outFileName,x4file.firstStrFile);
//	setFileDateN2(outFileName,x4file.firstStrFileDateTime);

	for (ii=0; ;ii++) {
	    ierr=x4file.readNextEntry();
	    msec1=System.currentTimeMillis();
//	    System.out.println(ii+")\t::ierr:"+ierr+" time:"+((msec1-msec0)/1000.)+"sec");
	    if (ierr!=0) break;
	    Vector vEntry=x4file.vEntry;
	    String Entry=x4file.Entry;
	    if (vEntry.size()>lnMax) lnMax=vEntry.size();
	    System.out.print("#"+(ii+1)+")\t--readNextEntry--"
		+"["+x4file.firstStrEntry+"]"
		+" #ln:"+vEntry.size()
		+" t:"+((msec1-msec0)/1000.)+"s"
		+"\n"
		);
//	    exfor2subr x4file0=new exfor2subr();
	    Vector vSubentOld=(Vector)hMasterEntry.get(Entry);
	    if (vSubentOld==null) vSubentOld=new Vector();
	    Vector vSubentUpd=getVSubents(vEntry);
	    Vector vSubentNew=vMergeVSubent(vSubentUpd,vSubentOld);
//	    System.out.print("-----Entry=["+Entry+"]");
	    int nSubOld=0;
	    if (vSubentOld.size()>0) nSubOld=vSubentOld.size();
	    System.out.println("    updateEntry:["+Entry+"] N2="+x4file.EntryN2+" nSub:old="+nSubOld+"/upd="+vSubentUpd.size()+"/new="+vSubentNew.size());
	    hMasterEntry.put(Entry,vSubentNew);

	    nEntries++;
	}

	x4file.closeInputFile();
	System.out.println("---closeInputFile--firstStrFile=["+x4file.firstStrFile+"]");
	System.out.println("---closeInputFile---lastStrFile=["+x4file.lastStrFile+"]");
	msec1=System.currentTimeMillis();
	System.out.println("---applyTransFile::time:"+((msec1-msec0)/1000.)+"sec"+" lnMax:"+lnMax);

	return nEntries;
    }

    public int splitExforFile2dir(String x4transName,String x4outDir)
    {
	int nEntries=0,i,ii,i2,isub,ierr;
	String str;
	if (x4outDir==null) x4outDir=this.x4outDir;
	if (x4outDir.equals("")) x4outDir=this.x4outDir;
	x4outDir=x4outDir.replaceAll("\\\\","/");
	if (!x4outDir.endsWith("/")) x4outDir+="/";
	this.x4outDir=x4outDir;
	System.out.println("\n---splitExforFile2dir---input file:["+x4transName+"] x4outDir:["+x4outDir+"]");

	long msec0,msec1=System.currentTimeMillis();
	msec0=System.currentTimeMillis();
	exfor2subr x4file=new exfor2subr();
	init_exfor2subr(x4file);

	ierr=x4file.openInputFile(x4transName);
	if (ierr!=0) {
	    System.out.println("Error opening:["+x4transName+"]");
	    System.exit(1);
	}
	System.out.println("---openInputFile---firstStrFile=["+x4file.firstStrFile+"]");
	x4LastFile=x4file;
//	outFileName=x4outDir+"00000head.exf";
//	x4file.outHead(outFileName,x4file.firstStrFile);
//	setFileDateN2(outFileName,x4file.firstStrFileDateTime);

	for (ii=0; ;ii++) {
	    ierr=x4file.readNextEntry();
	    msec1=System.currentTimeMillis();
	    if (ierr!=0) break;

	    Vector vEntry=x4file.vEntry;
	    String Entry=x4file.Entry;
	    String file1=Entry2dir(Entry);
	    System.out.print(ii+") Entry="+Entry+" file:"+file1+" #lines:"+vEntry.size()+"  \r");
	    if (ii%2000==0) System.out.println("");

	    x4outfile o5=new x4outfile(file1);
	    PrintWriter o5prn=o5.getPrintWriter();

	    x4file.printEntry(vEntry,o5prn);

	    if (o5!=null) {o5prn.flush();o5.close();o5=null;}
	    setFileDateN2(file1,x4file.EntryN2);
//	    System.out.println("---Entry::"+Entry+" ["+x4file.EntryN2+"]");

	    nEntries++;
	}

	x4file.closeInputFile();
	System.out.println("---closeInputFile--firstStrFile=["+x4file.firstStrFile+"]");
	System.out.println("---closeInputFile---lastStrFile=["+x4file.lastStrFile+"]");
	msec1=System.currentTimeMillis();
	System.out.println("---applyTransFile::time:"+((msec1-msec0)/1000.)+"sec");

	return nEntries;
    }

    public int outMasterFile(String outFileName)
    {
	int nEntries=0,i,ii,i2,isub,ierr,lnMax=0;
	String str;
	System.out.println("\n---outMasterFile---output file:["+outFileName+"]");

	long msec0,msec1=System.currentTimeMillis();
	msec0=System.currentTimeMillis();
	exfor2subr x4file=new exfor2subr();
	init_exfor2subr(x4file);

//	outFileName="tmp1master.exfor";
//	x4file.outHead(outFileName,x4LastFile.firstStrFile);
//	setFileDateN2(outFileName,x4LastFile.firstStrFileDateTime);

	x4outfile o5=new x4outfile(outFileName);
	PrintWriter o5prn=o5.getPrintWriter();

	Vector vec=hash2vector(hMasterEntry);
	vec=sortVecStr(vec);
	System.out.println("---MasterEntry::"+vec.size());
	getSubentLine(x4file.firstStrFile);
	str=x4file.getHead(fileHead,n1head,n2head,n3head,n4head,n5head);
	o5prn.println(str);
	for (i2=0; i2<vec.size(); i2++) {
	    Entry=(String)vec.elementAt(i2);
	    Vector vSubent=(Vector)hMasterEntry.get(Entry);
	    System.out.print(i2+"/"+vec.size()+": "+Entry+"---MasterEntry::#sub:"+vSubent.size()+"  \r");
	    if (i2%2000==0) System.out.println("");
	    x4file.printEntry(null,vSubent,o5prn);
	}
	str=x4file.getTail(fileHead,vec.size());
	o5prn.println(str);

	if (o5!=null) {o5prn.flush();o5.close();o5=null;}
	setFileDateN2(outFileName,x4file.EntryN2);

	return nEntries;
    }

    public int outMasterFileByEntries(String x4outDir)
    {
	int nEntries=0,i,ii,i2,isub,ierr,lnMax=0;
	String str;
	x4outDir=x4outDir.replaceAll("\\\\","/");
	if (!x4outDir.endsWith("/")) x4outDir+="/";
	this.x4outDir=x4outDir;
	System.out.println("\n---outMasterFileByEntries---output dir:["+x4outDir+"]");

	long msec0,msec1=System.currentTimeMillis();
	msec0=System.currentTimeMillis();
	exfor2subr x4file=new exfor2subr();
	init_exfor2subr(x4file);

	Vector vec=hash2vector(hMasterEntry);
	vec=sortVecStr(vec);
	System.out.println("---MasterEntry::"+vec.size());
	getSubentLine(x4file.firstStrFile);
	str=x4file.getHead(fileHead,n1head,n2head,n3head,n4head,n5head);
	for (i2=0; i2<vec.size(); i2++) {
	    Entry=(String)vec.elementAt(i2);
	    Vector vSubent=(Vector)hMasterEntry.get(Entry);
	    System.out.print(i2+"/"+vec.size()+": "+Entry+"---MasterEntry::#sub:"+vSubent.size()+"  \r");
	    if (i2%2000==0) System.out.println("");

	    String file1=Entry2dir(Entry);
	    x4outfile o5=new x4outfile(file1);
	    PrintWriter o5prn=o5.getPrintWriter();

	    x4file.printEntry(null,vSubent,o5prn);

	    if (o5!=null) {o5prn.flush();o5.close();o5=null;}
	    setFileDateN2(file1,x4file.EntryN2);
//	    System.out.println("---Entry::"+Entry+" ["+x4file.EntryN2+"]");
	}

	return nEntries;
    }

    public String Entry2dir(String Entry) {
	String str=x4outDir;
	mkdir(str);
//	str+="/"+Entry.substring(0,1);
	str+=Entry.substring(0,1);
	mkdir(str);
	str+="/"+Entry.substring(0,3);
	mkdir(str);
	str+="/"+Entry+".x4";
	return str;
    }
    public void mkdir(String fileName)
    {
	File f=new File(fileName);
	if (!f.exists()) {
	    boolean ok=f.mkdir();
//	    System.out.println("    ...mkdir: "+fileName+" OK="+ok);
	    return;
	}
	else {
	    if (f.isDirectory()) return;
	    else {
		System.out.println("...mkdir:["+fileName+"]===Error: there is file with such name.");
		System.exit(-1);
	    }
	}
    }

}
