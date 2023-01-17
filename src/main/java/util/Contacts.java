package util;

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

import models.Contact;
/* @Component - scan and register as a bean in Spring application. Means that the class can be used as a dependency
    in other classes 
*/ 
@Component("contacts")
public class Contacts {
    private static final Logger logger = LoggerFactory.getLogger(Contacts.class);
    /*
    Pass in the Contact object, model, application arguments we type in command line and the data directory
     */
    public void saveContact(Contact ctc, Model model, ApplicationArguments appArgs, String defaultDataDir) {
        // the file name will be the id 
        String dataFilename = ctc.getId();
        PrintWriter prntWriter = null;
        try {
            FileWriter fileWriter = new FileWriter(getDataDir(appArgs, defaultDataDir) + "/" + dataFilename);
            prntWriter = new PrintWriter(fileWriter);
            prntWriter.println(ctc.getName());
            prntWriter.println(ctc.getEmail());
            prntWriter.println(ctc.getPhoneNumber());
            prntWriter.println(ctc.getDateOfBirth().toString());
            prntWriter.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        model.addAttribute("contact", new Contact(ctc.getId(), ctc.getName(),
                ctc.getEmail(), ctc.getPhoneNumber(), ctc.getDateOfBirth()));
    }

    public void getContactById(Model model, String contactId, ApplicationArguments appArgs, String defaultDataDir) {
        Contact ctc = new Contact();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            Path filePath = new File(getDataDir(appArgs, defaultDataDir) + "/" + contactId).toPath();
            Charset charset = Charset.forName("UTF-8");
            List<String> stringList = Files.readAllLines(filePath, charset);
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
        List<String> optValues = null;
        String[] optValuesArr = null;
        Set<String> opsNames = appArgs.getOptionNames();
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

    public void getAllContactInURI(Model model, ApplicationArguments appArgs,
            String defaultDataDir) {
        Set<String> dataFiles = listFilesUsingJavaIO(getDataDir(appArgs, defaultDataDir));
        System.out.println("" + dataFiles);
        model.addAttribute("contacts", dataFiles.toArray(new String[dataFiles.size()]));
    }

    public Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }
}