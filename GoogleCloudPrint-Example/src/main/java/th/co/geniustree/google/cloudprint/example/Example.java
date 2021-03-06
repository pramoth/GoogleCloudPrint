/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.example;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jivesoftware.smack.XMPPConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import th.co.geniustree.google.cloudprint.api.GoogleCloudPrint;
import th.co.geniustree.google.cloudprint.api.exception.CloudPrintException;
import th.co.geniustree.google.cloudprint.api.model.Job;
import th.co.geniustree.google.cloudprint.api.model.JobListener;
import th.co.geniustree.google.cloudprint.api.model.JobStatus;
import th.co.geniustree.google.cloudprint.api.model.Printer;
import th.co.geniustree.google.cloudprint.api.model.PrinterStatus;
import th.co.geniustree.google.cloudprint.api.model.SubmitJob;
import th.co.geniustree.google.cloudprint.api.model.Ticket;
import th.co.geniustree.google.cloudprint.api.model.response.ControlJobResponse;
import th.co.geniustree.google.cloudprint.api.model.response.DeletePrinterResponse;
import th.co.geniustree.google.cloudprint.api.model.response.FecthJobResponse;
import th.co.geniustree.google.cloudprint.api.model.response.JobResponse;
import th.co.geniustree.google.cloudprint.api.model.response.PrinterInformationResponse;
import th.co.geniustree.google.cloudprint.api.model.response.RegisterPrinterResponse;
import th.co.geniustree.google.cloudprint.api.model.response.SearchPrinterResponse;
import th.co.geniustree.google.cloudprint.api.model.response.SharePrinterResponse;
import th.co.geniustree.google.cloudprint.api.model.response.SubmitJobResponse;
import th.co.geniustree.google.cloudprint.api.model.response.UpdatePrinterResponse;
import th.co.geniustree.google.cloudprint.api.util.PropertiesFileUtils;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class Example {

    private static final Logger LOG = LoggerFactory.getLogger(Example.class);
    private static final GoogleCloudPrint cloudPrint = new GoogleCloudPrint();
    private static Gson gson = new Gson(); 

    static {
        XMPPConnection.DEBUG_ENABLED = true;
        //System.setProperty("smack.debugEnabled", "true");
    }

    public static void main(String[] args) {
        try {
            Properties properties = PropertiesFileUtils.load("/account.properties");
            String email = properties.getProperty("email");
            String password = properties.getProperty("password");

            cloudPrint.connect(email, password, "geniustree-cloudprint-1.0");
            //searchAllPrinters();
            //searchPrinter("fax", PrinterStatus.ALL);
            //getJobs();
            //getJobOfPrinter("dc6929f5-8fdc-5228-1e73-c9dee3298445");
            //fetchJob("dc6929f5-8fdc-5228-1e73-c9dee3298445");
            //subscribeJob();
            //controlJob("7da91f4a-7faa-2a3d-5cd5-0f2a902a368b", JobStatus.QUEUED, 10, "success.");
            //sharePrinter("dc6929f5-8fdc-5228-1e73-c9dee3298445", "TARGET_EMAIL_TO_SHARE");
            //getPrinterInformation("dc6929f5-8fdc-5228-1e73-c9dee3298445");
            //getPrinterInformation("dc6929f5-8fdc-5228-1e73-c9dee3298445", PrinterStatus.ONLINE);
            //deletePrinter("12280cbe-6486-2c98-c65a-22083bd18b5b");
            //submitJob("810c1d39-981f-cd36-5fdc-951ea5e62613");
            registerPrinter();
            //updatePrinter();

        } catch (CloudPrintException ex) {
            LOG.warn("Exception", ex);
            System.exit(1);
        } catch (IOException ex) {
            LOG.warn("Exception", ex);
            System.exit(1);
        }
    }

    public static void updatePrinter() throws CloudPrintException {
        Printer printer = new Printer();
        printer.setId("a1dbe503-eb96-6d26-dc7b-a290a1cfaf3b");
        printer.setName("Adobe PDF2");

        UpdatePrinterResponse response = cloudPrint.updatePrinter(printer);
        if (!response.isSuccess()) {
            return;
        }

        LOG.debug("update printer response => {}", response.isSuccess() + ", " + response.getMessage());
    }

    public static void registerPrinter() throws CloudPrintException {
        InputStream inputStream = null;
        try {
            inputStream = Example.class.getResourceAsStream("/json/capabilities.json");
            String capabilities = IOUtils.toString(inputStream);
            Object object = gson.fromJson(capabilities, Object.class);

            Printer printer = new Printer();
            printer.setName("jittagorn pitakmetagoon");
            printer.setDisplayName("jittagorn pitakmetagoonp");
            printer.setProxy("pamarin");
            Set<String> tags = new HashSet<String>();
            tags.add("test");
            tags.add("register");
            printer.setTags(tags);
            printer.setCapsHash("1234");
            printer.setStatus("REGISTER");
            printer.setDescription("test register printer");
            printer.setCapabilities(object);
            printer.setDefaults(object);

            RegisterPrinterResponse response = cloudPrint.registerPrinter(printer);
            LOG.debug("response => {}", response);

        } catch (IOException ex) {
            LOG.warn("Exception", ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    LOG.warn("Exception", ex);
                }
            }
        }
    }

    public static void submitJob(String printerId) {
        InputStream jsonInputStream = null;
        InputStream imageInputStream = null;
        try {
            jsonInputStream = Example.class.getResourceAsStream("/json/submitJobTicketRequest.json");
            imageInputStream = Example.class.getResourceAsStream("/testImage.png");
            byte[] content = IOUtils.toByteArray(imageInputStream);

            String jsonTicket = IOUtils.toString(jsonInputStream);
            Ticket ticket = gson.fromJson(jsonTicket, Ticket.class);

            String json = gson.toJson(ticket);
            LOG.debug("json => {}", json);
            SubmitJob submitJob = new SubmitJob();
            submitJob.setContent(content);
            submitJob.setContentType("image/png");
            submitJob.setPrinterId(printerId);
            submitJob.setTag(Arrays.asList("koalar", "hippo", "cloud"));
            submitJob.setTicket(ticket);
            submitJob.setTitle("testImage.png");

            SubmitJobResponse response = cloudPrint.submitJob(submitJob);
            LOG.debug("submit job response => {}", response.isSuccess() + "," + response.getMessage());
            LOG.debug("submit job id => {}", response.getJob().getId());
            //controlJob(response.getJob().getId(), JobStatus.IN_PROGRESS, 100, "in progress.");
        } catch (Exception ex) {
            LOG.warn("Exception", ex);
        } finally {
            if (jsonInputStream != null) {
                try {
                    jsonInputStream.close();
                } catch (IOException ex) {
                    LOG.warn("Exception", ex);
                }
            }

            if (imageInputStream != null) {
                try {
                    imageInputStream.close();
                } catch (IOException ex) {
                    LOG.warn("Exception", ex);
                }
            }
        }
    }

    public static void subscribeJob() {
        cloudPrint.subScribeJob(new JobListener() {
            //
            @Override
            public void jobArrive(Job job, boolean status, String message) {
                if (status) {
                    try {
                        LOG.debug("job arrive => {}", job);
                        dowloadFile(job);
                        //controlJob(job.getId(), JobStatus.IN_PROGRESS, 100, "in progress.");
                        //do something...
                        String ticketResponse = cloudPrint.getJobTicket(job.getTicketUrl());
                        LOG.debug("ticketResponse => {}", ticketResponse);
                        controlJob(job.getId(), JobStatus.IN_PROGRESS, 100, "success.");
                    } catch (CloudPrintException ex) {
                        LOG.warn("Exception", ex);
                    }
                } else {
                    LOG.info("job arrive error message => {}", message);
                }
            }
        });
    }

    public static void fetchJob(String printerId) throws CloudPrintException {
        FecthJobResponse response = cloudPrint.fetchJob(printerId);
        if (!response.isSuccess()) {
            return;
        }

        for (Job job : response.getJobs()) {
            LOG.debug("job response => {}", job);
        }
    }

    public static void getJobOfPrinter(String printerId) throws CloudPrintException {
        JobResponse response = cloudPrint.getJobOfPrinter(printerId);
        if (!response.isSuccess()) {
            return;
        }

        for (Job job : response.getJobs()) {
            LOG.debug("job response => {}", job);
        }
    }

    public static void getJobs() throws CloudPrintException {
        JobResponse response = cloudPrint.getJobs();
        if (!response.isSuccess()) {
            return;
        }

        for (Job job : response.getJobs()) {
            LOG.debug("job response => {}", job);
        }
    }

    public static void getPrinterInformation(String printerId, PrinterStatus status) throws CloudPrintException {
        PrinterInformationResponse response = cloudPrint.getPrinterInformation(printerId, status);
        if (!response.isSuccess()) {
            return;
        }

        for (Printer printer : response.getPrinters()) {
            LOG.debug("printer information response => {}", printer);
        }
    }

    public static void getPrinterInformation(String printerId) throws CloudPrintException {
        PrinterInformationResponse response = cloudPrint.getPrinterInformation(printerId);
        if (!response.isSuccess()) {
            return;
        }

        for (Printer printer : response.getPrinters()) {
            LOG.debug("printer information response => {}", printer);
        }
    }

    public static void dowloadFile(Job job) throws CloudPrintException {
        LOG.debug("job => {}", job);
        File directory = new File("C:\\temp\\cloudPrint");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = FilenameUtils.removeExtension(job.getTitle()) + ".pdf";
        File outputFile = new File(directory, fileName);

        cloudPrint.downloadFile(job.getFileUrl(), outputFile);
    }

    public static void controlJob(String jobId, JobStatus status, int code, String message) throws CloudPrintException {
        ControlJobResponse response = cloudPrint.controlJob(jobId, status, code, message);
        LOG.debug("control job response=> {}", response.isSuccess() + ", " + response.getMessage());
    }

    public static void sharePrinter(String printerId, String shareEmail) throws CloudPrintException {
        SharePrinterResponse response = cloudPrint.sharePrinter(printerId, shareEmail);
        LOG.debug("share printer message => {}", response.isSuccess() + ", " + response.getMessage());
    }

    public static void searchPrinter(String query, PrinterStatus status) throws CloudPrintException {
        SearchPrinterResponse response = cloudPrint.searchPrinter(query, status);
        if (!response.isSuccess()) {
            return;
        }

        for (Printer printer : response.getPrinters()) {
            LOG.debug("printer => {}", printer);
        }
    }

    public static void searchAllPrinters() throws CloudPrintException {
        SearchPrinterResponse response = cloudPrint.searchPrinters();
        if (!response.isSuccess()) {
            return;
        }

        for (Printer printer : response.getPrinters()) {
            LOG.debug("printer => {}", printer);
            getPrinterInformation(printer.getId());
            //sharePrinter(printer.getId(), "TARGET_EMAIL_FOR_SHARE");
        }
    }

    public static void deletePrinter(String printerId) throws CloudPrintException {
        DeletePrinterResponse response = cloudPrint.deletePrinter(printerId);
        LOG.debug("delete printer response => {}", response.isSuccess() + ", " + response.getMessage());
    }
}
