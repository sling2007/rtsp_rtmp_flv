package org.springblade.flv.controller;

import lombok.AllArgsConstructor;
import org.springblade.flv.fegin.FvlFenginClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @ClassName FvlController
 * @Author qiaoyc
 * @Date 2023/11/15 8:31
 */
@RestController
@AllArgsConstructor
@RequestMapping("/fc")
@CrossOrigin(origins = "*")
public class FlvController {

	private final FvlFenginClient fvlFenginClient;

	@GetMapping("/rtspToFlv")
	public void rtspToFlv(@RequestParam String rtspUrl, HttpServletRequest request, HttpServletResponse response){
		fvlFenginClient.rtspToflv(rtspUrl,request,response);
	}

	@GetMapping("/test")
	public String test(){
		return "OK";
	}

}
