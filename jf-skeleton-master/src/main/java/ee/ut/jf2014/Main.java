package ee.ut.jf2014;

import java.io.IOException;

import ee.ut.jf2014.homework3.Homework;

public class Main {
    public static void main(String[] args) throws IOException {
    	if(args.length != 2)
		{
			System.err.println("Enter source and target!");
			System.exit(-1);
		}
		
		Homework hw = new Homework(args[0],args[1]);
		
        //Homework homework = new Homework(args[0],args[1]);

        //System.out.println(homework.square(4));
    }
}
