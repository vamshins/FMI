package edu.unm.missingisirs.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import edu.unm.missingisirs.model.IsirsFafsaModel;
import edu.unm.missingisirs.model.MissingIsirsAppModel;
import edu.unm.missingisirs.utils.IsirsUtil;

@Controller
@ComponentScan(basePackages = { "edu.unm.missingisirs.*" })
@PropertySource("classpath:project.properties")
@Scope("session")
@SessionAttributes(value = { "serverUploadSubDirPath", "serverReportPath", "reportFilesList" })
public class FileProcessController {
	
	private static final Logger logger = Logger.getLogger(FileProcessController.class);
	
	/*@Value("${uploadServerPath}")
	private String uploadServerPath;

	@Value("${username}")
	private String username;

	@Value("${password}")
	private String password;*/
	
	@Autowired
	private Environment env;

	@RequestMapping(value = "/main/upload", method = RequestMethod.GET)
	public String uploadFilesDisplayForm() {
		logger.info("Loading upload page");
		return "uploadfile";
	}
	
	@RequestMapping(value = "/main/savefiles", method = RequestMethod.POST)
	public String isirsSave(@ModelAttribute("uploadForm") MissingIsirsAppModel missingIsirsModelAttr, Model map, HttpServletRequest request) throws IllegalStateException, IOException {
		// String saveDirectory = "E:/ISIRS/";
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("cn") != null) {
			// session retrieved, continue with operations
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			logger.info(timeStamp);
			String uploadServerPath = env.getProperty("uploadServerPath");
//			Constants.serverUploadSubDirPath = uploadServerPath + timeStamp + "_" + env.getProperty("username");
			String serverUploadSubDirPath = uploadServerPath + timeStamp + "_" + session.getAttribute("username");
			
			logger.info("Session Id : " + session.getId());
		    logger.info("Logged in user : " + session.getAttribute("username"));
		    logger.info("CN : " + session.getAttribute("cn"));
			
			logger.info(session.getAttribute("serverUploadSubDirPath"));
			session.setAttribute("serverUploadSubDirPath", serverUploadSubDirPath);
			logger.info(session.getAttribute("serverUploadSubDirPath"));
			
			map.addAttribute("serverUploadSubDirPath", serverUploadSubDirPath);
			
			logger.info("Creating the directory to upload files on the server. Directory path: " + serverUploadSubDirPath);
			boolean success = (new File(serverUploadSubDirPath)).mkdir();
			
			if (success) {
				logger.info("Directory to upload files created successfully.");
				List<MultipartFile> isirsFiles = missingIsirsModelAttr.getFiles();
				List<String> fileNames = new ArrayList<String>();

				if (null != isirsFiles && isirsFiles.size() > 0) {
					for (MultipartFile multipartFile : isirsFiles) {

						String fileName = multipartFile.getOriginalFilename();
						if (!"".equalsIgnoreCase(fileName)) {
							// Handle file content - multipartFile.getInputStream()
							multipartFile.transferTo(new File(serverUploadSubDirPath + "/"+fileName));
							logger.info("Copied file \""+ serverUploadSubDirPath + "/"+fileName + "\" successfully");
							fileNames.add(fileName);
						}
					}
				}

				map.addAttribute("files", fileNames);
			}
			return "uploadfilesuccess";
		} else {
			return "loginpage";
		}
	}

	@RequestMapping(value = "/main/processfiles", method = RequestMethod.POST)
	public String isirsProcess(@ModelAttribute("processForm") MissingIsirsAppModel missingIsirsModelAttr, Model mapprocess, HttpServletRequest request) throws IllegalStateException, IOException, SQLException {
		// String saveDirectory = "E:/ISIRS/";
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("cn") != null) {

			IsirsFafsaModel ib = new IsirsFafsaModel();
			
			 String serverUploadSubDirPath = session.getAttribute("serverUploadSubDirPath").toString();
			
			logger.info("Processing request submitted. Processing files in the directory: " + serverUploadSubDirPath);
			ib.listBySsnMap = IsirsUtil.loadHashMap(serverUploadSubDirPath);
			String aidYear = missingIsirsModelAttr.getAidYear();
			logger.info("Aid Year: " + aidYear);
	//		System.exit(1);
			List<MultipartFile> isirsFiles = missingIsirsModelAttr.getFiles();
	
			if (null != isirsFiles && isirsFiles.size() > 0) {
				for (MultipartFile multipartFile : isirsFiles) {
	
					String fileName = multipartFile.getOriginalFilename();
					if (!"".equalsIgnoreCase(fileName)) {
						logger.info("--------------->>> " + fileName);
					}
				}
			}
			
			List<String> reportFilesList = new ArrayList<String>();
			reportFilesList.clear();
			try {
	
				// Finds the fafsas that are currently in suspense
				// (ROTIDEN) and puts that info into a HashMap
				IsirsUtil.findFafsaTransactionsInSuspense(ib.fafsaTransactionsInSuspenseBySsnMap, aidYear);
	
				IsirsUtil.findFafsasToLoad(ib.listBySsnMap, ib.fafsaTransToLoadList, ib.fafsaTransToNotLoadList, ib.fafsaTransKidsNotInDB, aidYear,
						ib.fafsasFromIsirFilesThatAreAlreadyInSuspenseList, ib.fafsaTransactionsInSuspenseBySsnMap);
	
				IsirsUtil.findFafsaTransactionsThatHaveNeverBeenLoaded(ib.listBySsnMap, ib.fafsaTransToLoadList, ib.fafsaTransKidsNotInDB,
						ib.fafsasThatHaveNeverBeenLoadedList, aidYear);
	
				reportFilesList = IsirsUtil.printReportAndFileToLoad(ib.fafsaTransToLoadList, ib.fafsaTransToNotLoadList, ib.fafsaTransToNotLoadLessList,
						ib.fafsaTransKidsNotInDB, session.getAttribute("username").toString(), ib.fafsasThatHaveNeverBeenLoadedList, ib.fafsasFromIsirFilesThatAreAlreadyInSuspenseList,
						serverUploadSubDirPath, aidYear);
				logger.info(reportFilesList);
				session.setAttribute("reportFilesList", reportFilesList);
				mapprocess.addAttribute("reportFilesList", reportFilesList);
			} catch (Exception e) {
				logger.fatal(e.getMessage());
			}
			
			/*Constants.reportFilesList.add("C:/ISIRS/20150310_142457/report/reportForISIR_stirlingcrow.txt");
			Constants.reportFilesList.add("C:/ISIRS/20150310_142457/report/IDSA16OP.txt");*/
			
			return "processcompleted";
		} else {
			return "loginpage";
		}
	}
	
	/**
	 * Size of a byte buffer to read/write file
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * Method for handling file download request from client
	 */
	@RequestMapping(value = "/main/downloadReportFile", method = RequestMethod.GET)
	public String reportDownload(HttpServletRequest request,
		HttpServletResponse response)
		throws IOException {
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("cn") != null) {
			final ServletContext context = request.getSession().getServletContext();
			final List<String> reportFiles = (List<String>) session.getAttribute("reportFilesList");
			processFileForDownload(response, context, reportFiles.get(0));
			return null;
		} else {
			return "loginpage";
		}
	}

	@RequestMapping(value = "/main/downloadIsdaFile", method = RequestMethod.GET)
	public String isdaDownload(HttpServletRequest request,
		HttpServletResponse response)
		throws IOException {
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("cn") != null) {
			final ServletContext context = request.getSession().getServletContext();
			final List<String> reportFiles = (List<String>) session.getAttribute("reportFilesList");
			processFileForDownload(response, context, reportFiles.get(1));
			return null;
		} else {
			return "loginpage";
		}
	}

	private void processFileForDownload(final HttpServletResponse response,
		final ServletContext context, final String fullPath)
		throws FileNotFoundException, IOException {
		// construct the complete absolute path of the file
		final File downloadFile = new File(fullPath);
		final FileInputStream inputStream = new FileInputStream(downloadFile);
		// get MIME type of the file
		String mimeType = context.getMimeType(fullPath);
		if (mimeType == null) {
			// set to binary type if MIME mapping not found
			mimeType = "application/octet-stream";
		}
		System.out.println("MIME type: " + mimeType);
		// set content attributes for the response
		response.setContentType(mimeType);
		response.setContentLength((int) downloadFile.length());
		// set headers for the response
		final String headerKey = "Content-Disposition";
		final String headerValue = String.format("attachment; filename=\"%s\"",
			downloadFile.getName());
		response.setHeader(headerKey, headerValue);
		// get output stream of the response
		final OutputStream outStream = response.getOutputStream();
		final byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		// write bytes read from the input stream into the output stream
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, bytesRead);
		}
		inputStream.close();
		outStream.close();
	}
}
