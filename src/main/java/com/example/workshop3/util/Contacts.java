package com.example.workshop3.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import com.example.workshop3.models.Contact;

/* @Component - scan and register as a bean in Spring application. Means that the class can be used as a dependency
    in other classes 
*/
@Component("contacts")
public class Contacts {
    private static final Logger logger = LoggerFactory.getLogger(Contacts.class);

    /*
     * Pass in the Contact object, model, application arguments we type in command
     * line and the data directory
     */
    public void saveContact(Contact ctc, Model model, ApplicationArguments appArgs, String defaultDataDir) {
        // the file name will be the id
        String dataFilename = ctc.getId();
        // PrintWriter, which is used for writing to a file or other output stream
        // we use null - setting it to have no value or reference
        // typically be used in a try-catch block where the variable is initialized
        // inside the try block and closed (if not null) inside the catch block.
        PrintWriter prntWriter = null;
        try {
            // FileWriter is initialized with a file path, which is created by concatenating
            // the results of the getDataDir(appArgs, defaultDataDir) eg. data/abcd1234
            FileWriter fileWriter = new FileWriter(getDataDir(appArgs, defaultDataDir) + "/" + dataFilename);
            // pass in the file that we want to write the data to
            prntWriter = new PrintWriter(fileWriter);
            prntWriter.println(ctc.getName());
            prntWriter.println(ctc.getEmail());
            prntWriter.println(ctc.getPhoneNumber());
            prntWriter.println(ctc.getDateOfBirth().toString());
            prntWriter.close();
        } catch (IOException e) {
            // retrieve the message associated with the exception, which provides more
            // information about what went wrong. This message will be logged along with the
            // error message.
            logger.error(e.getMessage());
        }

        model.addAttribute("contact", new Contact(ctc.getId(), ctc.getName(),
                ctc.getEmail(), ctc.getPhoneNumber(), ctc.getDateOfBirth()));
    }

    public void getContactById(Model model, String contactId, ApplicationArguments appArgs, String defaultDataDir) {
        // we create a new contact object
        Contact ctc = new Contact();
        // we format the date of birth of the contact
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            // The file path is constructed by concatenating the result of the getDataDir method, which retrieves the location of the contact data directory, with the contact's ID and the file separator /. The toPath() method is then called on the File object to convert it into a Path object. The Path object can be used to interact with the file on the file system, such as reading it using the Files.readAllLines method
            Path filePath = new File(getDataDir(appArgs, defaultDataDir) + "/" + contactId).toPath();
            Charset charset = Charset.forName("UTF-8");
            //  The method reads all the lines from the file into a list of strings, each string representing a line from the file. The Charset object charset is passed as a second parameter to the method, which is used to decode the bytes read from the file into characters.
            List<String> stringList = Files.readAllLines(filePath, charset);
            //  extracting the contact information from the stringList and setting it to the Contact object
            ctc.setId(contactId);
            ctc.setName(stringList.get(0));
            ctc.setEmail(stringList.get(1));
            ctc.setPhoneNumber(stringList.get(2));
            LocalDate dob = LocalDate.parse(stringList.get(3), formatter);
            ctc.setDateOfBirth(dob);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Contact info not found");
        }

        model.addAttribute("contact", ctc);
    }

    // create a method to get the data directory
    private String getDataDir(ApplicationArguments appArgs, String defaultDataDir) {
        String dataDirResult = "";
        // store the list of values for the first option name passed as an argument to the program.
        List<String> optValues = null;
        // array version of the optValues list
        String[] optValuesArr = null;
        // argument pass in --foo=bar --debug will return ["foo","debug"]
        Set<String> opsNames = appArgs.getOptionNames();
        // toArray() method returns an array of Object type, it is then casted to String[] so that it can be used with string operations.This optNamesArr array will be used later in the code to check the length of the array and if it has at least one element, the method uses the first element of this array as the option name to get the option values
        String[] optNamesArr = opsNames.toArray(new String[opsNames.size()]);
        if (optNamesArr.length > 0) {
            optValues = appArgs.getOptionValues(optNamesArr[0]);
            optValuesArr = optValues.toArray(new String[optValues.size()]);
            dataDirResult = optValuesArr[0];
        } else {
            dataDirResult = defaultDataDir;
        }

        return dataDirResult;
    }
    // used to retrieve and display a list of contact files in a web application.
    public void getAllContactInURI(Model model, ApplicationArguments appArgs,
            String defaultDataDir) {
        Set<String> dataFiles = listFilesUsingJavaIO(getDataDir(appArgs, defaultDataDir));
        System.out.println("" + dataFiles);
        model.addAttribute("contacts", dataFiles.toArray(new String[dataFiles.size()]));
    }
    // get a set of all files within a directory
    public Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }
}