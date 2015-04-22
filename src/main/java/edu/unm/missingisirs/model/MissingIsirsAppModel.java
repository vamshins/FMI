package edu.unm.missingisirs.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;


public class MissingIsirsAppModel {
	private String fileFullPathName;
	private String aidYear;
	private List<MultipartFile> isirsFiles;
	public String serverUploadSubDirPath;
	public String serverReportPath;
	public ArrayList<String> reportFilesList = new ArrayList<String>();

	public String getFileFullPathName() {
		return fileFullPathName;
	}

	public void setFileFullPathName(String fileFullPathName) {
		this.fileFullPathName = fileFullPathName;
	}
	
	public String getAidYear() {
		return aidYear;
	}

	public void setAidYear(String aidYear) {
		this.aidYear = aidYear;
	}
	 
    public List<MultipartFile> getFiles() {
        return isirsFiles;
    }
 
    public void setFiles(List<MultipartFile> files) {
        this.isirsFiles = files;
    }
    
    public ArrayList<String> getReportFilesList() {
		return reportFilesList;
	}	
}
