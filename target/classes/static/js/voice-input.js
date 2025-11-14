// 语音输入组件
const voiceInputComponent = {
    data() {
        return {
            isRecording: false,
            mediaRecorder: null,
            audioChunks: [],
            recordingDuration: 0,
            durationTimer: null,
            isSupported: 'mediaDevices' in navigator && 'getUserMedia' in navigator.mediaDevices,
            buttonText: '开始语音输入',
            buttonClass: 'btn btn-primary'
        };
    },
    props: {
        // 绑定的输入框ID
        targetInputId: {
            type: String,
            required: true
        },
        // 最大录音时长（秒）
        maxDuration: {
            type: Number,
            default: 60
        },
        // 按钮尺寸
        size: {
            type: String,
            default: 'normal', // normal, small, large
            validator: value => ['normal', 'small', 'large'].includes(value)
        }
    },
    computed: {
        buttonSizeClass() {
            return this.size === 'small' ? 'btn-small' : 
                   this.size === 'large' ? 'btn-large' : '';
        },
        formattedDuration() {
            const minutes = Math.floor(this.recordingDuration / 60);
            const seconds = this.recordingDuration % 60;
            return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        }
    },
    methods: {
        async startRecording() {
            if (!this.isSupported) {
                this.showError('您的浏览器不支持语音输入功能');
                return;
            }

            try {
                // 请求麦克风权限
                const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
                
                // 创建MediaRecorder实例
                this.mediaRecorder = new MediaRecorder(stream);
                this.audioChunks = [];
                this.recordingDuration = 0;
                
                // 监听录音数据
                this.mediaRecorder.addEventListener('dataavailable', event => {
                    if (event.data.size > 0) {
                        this.audioChunks.push(event.data);
                    }
                });
                
                // 监听录音停止事件
                this.mediaRecorder.addEventListener('stop', () => {
                    // 停止所有音轨
                    stream.getTracks().forEach(track => track.stop());
                    
                    // 处理录音数据
                    this.processRecording();
                    
                    // 重置按钮状态
                    this.isRecording = false;
                    this.buttonText = '开始语音输入';
                    this.buttonClass = 'btn btn-primary';
                    
                    // 清除计时器
                    if (this.durationTimer) {
                        clearInterval(this.durationTimer);
                        this.durationTimer = null;
                    }
                });
                
                // 开始录音
                this.mediaRecorder.start(1000); // 每秒保存一次数据
                this.isRecording = true;
                this.buttonText = '停止录音';
                this.buttonClass = 'btn btn-danger';
                
                // 开始计时
                this.startDurationTimer();
                
            } catch (error) {
                console.error('开始录音失败:', error);
                this.showError('无法访问麦克风，请检查权限设置');
            }
        },
        
        stopRecording() {
            if (this.mediaRecorder && this.mediaRecorder.state !== 'inactive') {
                this.mediaRecorder.stop();
            }
        },
        
        startDurationTimer() {
            this.durationTimer = setInterval(() => {
                this.recordingDuration++;
                
                // 达到最大录音时长时自动停止
                if (this.recordingDuration >= this.maxDuration) {
                    this.stopRecording();
                }
            }, 1000);
        },
        
        async processRecording() {
            try {
                // 显示加载状态
                this.showLoading('正在处理语音...');
                
                // 创建音频Blob
                const audioBlob = new Blob(this.audioChunks, { type: 'audio/wav' });
                
                // 准备FormData
                const formData = new FormData();
                formData.append('file', audioBlob, 'recording.wav');
                
                // 调用语音识别API
                const response = await window.voiceApi.transcribeVoice(formData);
                
                if (response && response.data && response.data.code === 0) {
                    const transcribedText = response.data.data?.transcribedText || '';
                    
                    // 将识别的文本填充到目标输入框
                    const targetInput = document.getElementById(this.targetInputId);
                    if (targetInput) {
                        // 保留现有内容，并追加识别结果
                        const currentValue = targetInput.value;
                        targetInput.value = currentValue ? `${currentValue}\n${transcribedText}` : transcribedText;
                        
                        // 触发input事件以更新Vue绑定
                        targetInput.dispatchEvent(new Event('input', { bubbles: true }));
                        
                        // 聚焦到输入框，方便用户编辑
                        targetInput.focus();
                    }
                    
                    this.showSuccess('语音识别成功');
                } else {
                    throw new Error(response?.data?.message || '语音识别失败');
                }
            } catch (error) {
                console.error('语音处理失败:', error);
                this.showError('语音识别失败，请重试');
            } finally {
                // 隐藏加载状态
                this.hideLoading();
            }
        },
        
        toggleRecording() {
            if (this.isRecording) {
                this.stopRecording();
            } else {
                this.startRecording();
            }
        },
        
        // 工具方法：显示错误消息
        showError(message) {
            if (window.ElMessage) {
                window.ElMessage.error(message);
            } else {
                alert(message);
            }
        },
        
        // 工具方法：显示成功消息
        showSuccess(message) {
            if (window.ElMessage) {
                window.ElMessage.success(message);
            }
        },
        
        // 工具方法：显示加载状态
        showLoading(message) {
            if (window.ElMessage) {
                this.loadingInstance = window.ElMessage.loading({
                    message: message,
                    duration: 0,
                    forbidClick: true
                });
            }
        },
        
        // 工具方法：隐藏加载状态
        hideLoading() {
            if (this.loadingInstance) {
                this.loadingInstance.close();
            }
        }
    },
    template: `
        <div class="voice-input-container">
            <button 
                :class="[buttonClass, buttonSizeClass]" 
                @click="toggleRecording" 
                :disabled="!isSupported"
                title="点击开始/停止录音"
            >
                <span v-if="isRecording">
                    <i class="el-icon-video-camera"></i> {{ buttonText }} ({{ formattedDuration }})
                </span>
                <span v-else>
                    <i class="el-icon-microphone"></i> {{ buttonText }}
                </span>
            </button>
            <div v-if="!isSupported" class="voice-not-supported">
                浏览器不支持语音输入
            </div>
        </div>
    `,
    styles: `
        .voice-input-container {
            display: inline-block;
            position: relative;
        }
        .voice-not-supported {
            position: absolute;
            bottom: -20px;
            left: 0;
            font-size: 12px;
            color: #909399;
            white-space: nowrap;
        }
    `
};

// 导出组件定义，供Vue 3应用使用
if (typeof module !== 'undefined' && module.exports) {
    // CommonJS导出
    module.exports = voiceInputComponent;
} else if (typeof define === 'function' && define.amd) {
    // AMD导出
    define([], function() { return voiceInputComponent; });
} else {
    // 全局导出
    window.voiceInputComponent = voiceInputComponent;
}

// 自动注册为Vue 3插件
if (window.Vue && window.Vue.createApp) {
    // 为Vue 3创建插件
    window.Vue.voiceInputPlugin = {
        install: function(app) {
            app.component('voice-input', voiceInputComponent);
        }
    };
}
