package com.travelai.controller;

import com.travelai.dto.Result;
import com.travelai.service.VoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    @Autowired
    private VoiceService voiceService;

    // 语音转文字接口
    @PostMapping("/transcribe")
    public Result<Map<String, String>> transcribeVoice(@RequestParam("file") MultipartFile file) {
        String text = voiceService.transcribeVoice(file);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("transcribedText", text);
        return Result.success(resultMap);
    }
}
