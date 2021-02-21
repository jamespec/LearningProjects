package com.pecoraro.avro;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.File;
import java.io.IOException;

public class CreateUsers
{
    public static void main(String[] args)
    {
        User user1 = new User();
        user1.setName("James");
        user1.setFavoriteFood("Cheeseburger");
        user1.setFavoriteColor("Yellow");
        // Leave out favorite number for null
        // user1.setFavoriteNumber(42);

        // Use null when necessary
        User user2 = new User("Elizabeth", 5, "Blue", null);

        // Again, all fields must be specified, though, defaults from the schema will be used as necessary.
        User user3 = User.newBuilder()
                        .setName("Mary")
                        .setFavoriteColor(null)
                        .setFavoriteFood("Mac and Cheese")
                        .setFavoriteNumber(9)
                        .build();

        System.out.println( user1 );
        System.out.println( user2 );
        System.out.println( user3 );

        // Serialize user1, user2 and user3 to disk
        DatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);

        try( DataFileWriter<User> dataFileWriter = new DataFileWriter<User>(userDatumWriter) ) {
            dataFileWriter.create(user1.getSchema(), new File("users.avro"));
            long start = System.nanoTime();
            for(int i=0; i<100000; i++) {
                dataFileWriter.append(user1);
                dataFileWriter.append(user2);
                dataFileWriter.append(user3);
            }
            long end = System.nanoTime();
            System.out.println("File created, data written in " + (end-start)/1000000.0 + " msecs");

        } catch( IOException e ) {
            e.printStackTrace();
        }


        File file = new File("users.avro");
        DatumReader<User> userDatumReader = new SpecificDatumReader<User>(User.class);
        try( DataFileReader<User> dataFileReader = new DataFileReader<User>(file, userDatumReader) ) {
            User user = null;
            int max = 10; // print only first 10
            long start = System.nanoTime();
            while (dataFileReader.hasNext()) {
                user = dataFileReader.next(user);
                if( max-- > 0 )
                    System.out.println(user);
            }
            long end = System.nanoTime();
            System.out.println("File created, data written in " + (end-start)/1000000.0 + " msecs");
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
