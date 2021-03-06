package com.nh.saerok.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nh.saerok.dto.Diary;
import com.nh.saerok.dto.Photo;
import com.nh.saerok.service.DiaryService;

@MapperScan(basePackages = { "com.nh.saerok.mapper" })
@CrossOrigin
@RestController
public class DiaryController {

	@Autowired
	ServletContext servletContext;

	@Autowired
	DiaryService service;

	@GetMapping(value = "/diary/{baby_no}")
	public List<Diary> selectAll(@PathVariable String baby_no) {
		List<Diary> list = null;
		try {
			list = service.selectAll(baby_no);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@GetMapping(value = "/diary/photo/{diary_no}")
	public List<Photo> selectPhotos(@PathVariable String diary_no) {
		List<Photo> list = null;
		try {
			list = service.selectPhotos(diary_no);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@GetMapping(value = "/diary/{baby_no}/{year}/{month}")
	public List<Diary> selectByDate(@PathVariable String baby_no, @PathVariable String year,
			@PathVariable String month) {
		List<Diary> list = null;
		try {
			list = service.selectByDate(baby_no, year, month);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@GetMapping(value = "/diary/{baby_no}/{num}")
	public Diary selectOne(@PathVariable String baby_no, @PathVariable String num) {
		Diary diary = null;
		try {
			diary = service.selectOne(baby_no, num);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return diary;
	}

	@DeleteMapping(value = "/diary/{num}")
	public int delete(@PathVariable String num) {
		try {
			return service.delete(num);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@PutMapping(value = "/diary")
	public int update(@RequestBody Diary diary) {
		try {
			return service.update(diary);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	// ?????? ?????????(db?????? ?????? ??? ???)
	@PostMapping(value = "/upload/profile/{type}")
	public int ProfileUpload(@PathVariable String type, @RequestParam MultipartFile[] multipartFiles) throws FileNotFoundException {
		System.out.println("?????? ??????: " + multipartFiles.length);
		System.out.println("type: " + type);
		try {
			for (int i = 0; i < multipartFiles.length; i++) {
				if(multipartFiles[i] != null && !multipartFiles[i].isEmpty()){	
					String fileName = multipartFiles[i].getOriginalFilename(); // ?????? ?????? ??????
					String realPath = 
							"C:\\ssafy\\NHhackathon_ACARDI\\front-end\\nhhackathon_acardi\\src\\assets\\img";
					String today = new SimpleDateFormat("yyMMdd").format(new Date()); // ?????? ??????
					String saveFolder = null;  // ?????? ?????? ?????? (??? ????????? ???????????? ??????)
					
					String baby_no = service.maxBabyNo();
					int maxBaby_no = Integer.parseInt(baby_no) + 1;
					System.out.println(maxBaby_no);
					
					
					saveFolder = realPath + File.separator + maxBaby_no + File.separator + type;
					File folder = new File(saveFolder);
					if(!folder.exists())
						folder.mkdirs();
					System.out.println(saveFolder);
					Photo photo = new Photo();
	
					if (!fileName.isEmpty()) { // abc-asdfasf-asdfs-fd.png
						String saveFileName = null;

						saveFileName = "profile.jpg"; // ?????? ?????? profile.jpg??? ?????? ????????? ?????? ?????????
					
						File file = new File(saveFolder + "\\"+ saveFileName);
						multipartFiles[i].transferTo(file);
					}
				}				
			}	
			return 1; 
		} catch (IOException e) {
				e.printStackTrace();
				return 0;
		}
	}

	@PostMapping(value = "/upload/{baby_no}/{type}")
	public int fileUpload(@PathVariable String baby_no, @PathVariable String type, @RequestParam MultipartFile[] multipartFiles) throws FileNotFoundException {
		System.out.println("?????? ??????" + multipartFiles.length);
		System.out.println(baby_no + " " + type);
		try {
			for (int i = 0; i < multipartFiles.length; i++) {
				if(multipartFiles[i] != null && !multipartFiles[i].isEmpty()){	
					String fileName = multipartFiles[i].getOriginalFilename(); // ?????? ?????? ??????
					String realPath = 
							"C:\\ssafy\\NHhackathon_ACARDI\\front-end\\nhhackathon_acardi\\src\\assets\\img";
					String today = new SimpleDateFormat("yyMMdd").format(new Date()); // ?????? ??????
					String saveFolder = null;  // ?????? ?????? ?????? (??? ????????? ???????????? ??????)
					if (type.equals("diary")) {
						//System.out.println("diary??????");
						saveFolder = realPath + File.separator + baby_no +  File.separator 
								+ type + File.separator + today;
					} else {
						saveFolder = realPath + File.separator + baby_no +  File.separator 
								+ type;
					}
					File folder = new File(saveFolder);
					if(!folder.exists())
						folder.mkdirs();
					System.out.println(saveFolder);
					Photo photo = new Photo();
	
					if (!fileName.isEmpty()) { // abc-asdfasf-asdfs-fd.png
						String saveFileName = null;
						if (type.equals("diary")) {
							//System.out.println("diary???");
							saveFileName = UUID.randomUUID().toString() + ".jpg";
						}else {
							saveFileName = "profile.jpg";
						}
						photo.setSave_path(saveFolder);
						photo.setUpload_name(fileName);
						photo.setSave_name(saveFileName);
						File file = new File(saveFolder + "\\"+ saveFileName);
						multipartFiles[i].transferTo(file);
					}
					
					String diary_no = service.maxId();// ???????????? ?????? ?????????
					photo.setDiary_no(diary_no);
					service.savePhoto(photo);
			
				}				
			}	
			return 1; 
		} catch (IOException e) {
				e.printStackTrace();
				return 0;
		}
	}
	

	// update photos
	@PutMapping(value = "/update/{baby_no}/{type}")
	public int fileUpdate(@RequestParam("file") MultipartFile[] multipartFiles, @RequestParam("info") String[] nums, 
			@PathVariable String baby_no, @PathVariable String type) throws FileNotFoundException {
		int result = 0;
		try {
			for (int i = 0; i < multipartFiles.length; i++) {
				System.out.println(multipartFiles[i].getOriginalFilename());
				if (multipartFiles[i] != null && !multipartFiles[i].isEmpty()) {
					String fileName = multipartFiles[i].getOriginalFilename(); // ?????? ?????? ??????
					String realPath = "C:\\ssafy\\NHhackathon_ACARDI\\front-end\\nhhackathon_acardi\\src\\assets\\img"; // ??????
																													// ??????
					String today = new SimpleDateFormat("yyMMdd").format(new Date()); // ?????? ??????
					String saveFolder = null; // ?????? ?????? ?????? (??? ????????? ???????????? ??????)
					if (type == "diary") {
						saveFolder = realPath + File.separator + baby_no +  File.separator 
								+ type + File.separator + today;
					} else {
						saveFolder = realPath + File.separator + baby_no +  File.separator 
								+ type;
					}
					File folder = new File(saveFolder);
					if (!folder.exists())
						folder.mkdirs();
					System.out.println(saveFolder);

					Photo photo = new Photo();

					if (!fileName.isEmpty()) {
						String saveFileName = null;
						if (type=="diary") {
							saveFileName = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf('.'));
						}else {
							saveFileName = "profile" + fileName.substring(fileName.lastIndexOf('.'));
						}
						photo.setSave_path(saveFolder);
						photo.setUpload_name(fileName);
						photo.setSave_name(saveFileName);
						photo.setNo(nums[i]);
						
						File file = new File(saveFolder + "\\" + saveFileName);

						multipartFiles[i].transferTo(file);
						result += service.updatePhoto(photo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	// ???????????? ??????
	@PostMapping(value = "/diary")
	public int insert(@RequestBody Diary d) throws FileNotFoundException {
		System.out.println("insert");
		int n = service.insert(d);
		String diary_no = service.maxId();
		return 1;
	}

}
