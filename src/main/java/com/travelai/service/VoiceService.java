package com.travelai.service;

import org.springframework.web.multipart.MultipartFile;

public interface VoiceService {
    // 语音文件转文字
    String transcribeVoice(MultipartFile file);
    
    // 获取语音识别配置
    void initVoiceRecognizer();
}
