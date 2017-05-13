package com.dav;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dav.dao.DocumentDao;
import com.dav.entity.Document;
import com.dav.service.StorageFileNotFoundException;
import com.dav.service.StorageService;
import com.dav.service.UploadS3;
import com.dav.service.UploadService;



import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import javax.websocket.server.PathParam;

@Controller
public class FileUploadController {

	@Autowired
	private DocumentDao documentDao;

	private final StorageService storageService;

	@Autowired
	public FileUploadController(StorageService storageService) {
		this.storageService = storageService;
	}

	@GetMapping("/")
	public String listUploadedFiles(Model model) throws IOException {

		model.addAttribute("files",
				storageService.loadAll()
						.map(path -> MvcUriComponentsBuilder
								.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
								.build().toString())
						.collect(Collectors.toList()));
		Document document = new Document();
		model.addAttribute("document", document);
		return "welcome";
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes)
			throws IllegalStateException, IOException {

		File convFile = storageService.store(file);

		UploadService service = new UploadService();
		String url = service.Upload(convFile);

		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "! And Uploaded to Drive View: " + url);
		return "redirect:/";
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/{id}")
	public String findById(@PathVariable("id")Integer id, Model model) {
		Document document = documentDao.findOne(id);
		model.addAttribute("document", document);
		return "welcome";
	}
	
	@RequestMapping("insert")
	public String insert(ModelMap model,
			@ModelAttribute("document")Document document,@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
		System.out.println(document.getTitle());

		File convFile = storageService.store(file);

		UploadS3 service = new UploadS3();
		String url = service.upload(convFile);

		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "! And Uploaded to Drive View: " + url);
		document.setUrl(url);
		documentDao.save(document);
		System.out.println(document.getId());
		return "redirect:/";
	}
	
	@ModelAttribute("documents")
	public List<Document> getDocuments(){
		return (List<Document>) documentDao.findAll();
	}
}
