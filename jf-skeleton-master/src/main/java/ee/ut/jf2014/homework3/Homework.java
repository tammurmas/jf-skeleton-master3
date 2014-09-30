package ee.ut.jf2014.homework3;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.LinkOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Homework {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private final Path source;
	private final Path target;
	private static final CopyOption[] copyOptions = new CopyOption[]{
												  StandardCopyOption.REPLACE_EXISTING,
												  StandardCopyOption.COPY_ATTRIBUTES
												};
	
	private static final LinkOption[] linkOptions = new LinkOption[]{
												LinkOption.NOFOLLOW_LINKS
												};
	
	public Homework(Path source, Path target)
	{
		this.source = source;
		this.target = target;
	}
	
	public static void main(String[] args) throws IOException
	{
		Logger log = LoggerFactory.getLogger("main");
		
		if(args.length != 2)
		{
			System.err.println("Enter source and target!");
			System.exit(-1);
		}
		
		Path s = null;
		Path t = null;
		
		//check source
		try
		{
			s = FileSystems.getDefault().getPath(args[0]).toRealPath(linkOptions);
		}
		catch(InvalidPathException e)
		{
			System.err.println("Source invalid!");
			e.printStackTrace();
			System.exit(-1);
		} catch (NoSuchFileException e) {
		    System.err.format("%s: no such" + " file or directory%n", s);
		    e.printStackTrace();
		    System.exit(-1);//is the correct way to do it???
		} catch (IOException e) {
		    System.err.format("%s%n", e);
		}
		
		//check target
		try
		{
			t = FileSystems.getDefault().getPath(args[1]).toRealPath(linkOptions);
		}
		catch(InvalidPathException e)
		{
			System.err.println("Target invalid!");
			e.printStackTrace();
			System.exit(-1);
		}
		//target does not exist, create it
		catch (NoSuchFileException e) {
			Files.createDirectories(t);
		} catch (IOException e) {
		    System.err.format("%s%n", e);
		}
		
		Homework hw = new Homework(s,t);
		
		System.out.println("Source: "+hw.source.toString());
		System.out.println("Target: "+hw.target.toString());
		
		//iterate over source dir on startup
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(hw.source)) {
			 
		       for (Path entry: stream) {
		    	   //files only
		    	   if(Files.isRegularFile(entry,linkOptions))
		    	   {
		    		   Path filename = hw.target.resolve(entry.getFileName());
		    		   
		    		   //no such file in target, copy
		    		   if(!Files.exists(filename))
		    		   {
		    			   hw.copyFile(entry, filename);
		    		   }
		    		   else
		    		   {
		    			   //check if modified in-between then copy
		    			   if(!Files.getLastModifiedTime(entry, linkOptions).equals(Files.getLastModifiedTime(filename, linkOptions)))
		    			   {
		    				   hw.copyFile(entry, filename);
		    			   }
		    				   
		    		   }
		    	   }
		    		   
		       }
		}
		//remove files from target not present in source
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(hw.target)) {
			for (Path entry: stream) {
				if(Files.isRegularFile(entry,linkOptions))
		    	{
		    	   Path filename = hw.source.resolve(entry.getFileName());
		    	   
		    	   //no such file in target, delete
	    		   if(!Files.exists(filename))
	    		   {
	    			   hw.deleteFile(entry);
	    		   }
		    	}
			}
		}
		
		
		
	}//main
	
	public void deleteFile(Path path) throws IOException//throws clause needed here???
	{
		try {
			Files.delete(path);
			System.out.println("Deleted \""+path.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void copyFile(Path source, Path destination) throws IOException
	{
		try
		{
			Path newPath = Files.copy(source, destination, copyOptions);
			System.out.println("Copied from \""+source.toString()+"\" to \""+newPath.toString());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}