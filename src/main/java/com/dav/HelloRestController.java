package com.dav;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.dav.dao.DocumentDao;
import com.dav.entity.*;

@Controller
@RequestMapping("/api")
public class HelloRestController {
	
	  @Autowired
	  private DocumentDao documentDao;
	
	  @RequestMapping("/get-by-id")
	  @ResponseBody
	  public String getByEmail(Integer id) {
	    String documentId = "";
	    try {
	      Document document = documentDao.findById(id);
	      documentId = String.valueOf(document.getId());
	    }
	    catch (Exception ex) {
	      return "User not found";
	    }
	    return "The user id is: " + documentId;
	  }
}
