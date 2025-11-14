// 创建axios实例
const request = axios.create({
    baseURL: '',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
});

// 请求拦截器
request.interceptors.request.use(
    config => {
        // 首先尝试从localStorage获取token
        let token = localStorage.getItem('token');

        // 添加token到请求头
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        // 始终包含credentials，确保cookies被发送
        config.credentials = 'include';

        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// 响应拦截器
request.interceptors.response.use(
    response => {
        //response.data 为 Result{code, data, message}
        const result = response.data;
        if (result.code == 0) {
            return result.data;
        } else if (result.code === 401) {
            ElementPlus.ElMessage.error('登录过期，请重新登录');
            // 清除过期token
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            // 跳转到登录页
            window.location.href = 'login.html';
        } else {
            return Promise.reject(result.message || '请求失败');
        }
    },
    error => {
        // 网络错误
        return Promise.reject(error);
    }
);


// 认证相关API
const authApi = {
    // 用户注册
    register: (data) => request.post('/api/auth/register', data),

    // 用户登录
    login: (data) => request.post('/api/auth/login', data),

    // 用户退出
    logout: () => request.post('/api/auth/logout')
};

// 行程相关API
const travelPlanApi = {
    // 获取用户所有行程
    getUserTravelPlans: async () => request.get('/api/travel-plans'),

    // 获取行程详情
    getTravelPlanById: async (id) => request.get(`/api/travel-plans/${id}`),

    // 创建新行程
    createTravelPlan: async (data) => request.post('/api/travel-plans', data),

    // 更新行程
    updateTravelPlan: async (id, data) => request.put(`/api/travel-plans/${id}`, data),

    // 删除行程
    deleteTravelPlan: async (id) => request.delete(`/api/travel-plans/${id}`)
};

// 费用相关API
const expenseApi = {
    // 获取行程的所有费用记录
    getExpensesByTravelPlanId: (travelPlanId) => request.get(`/api/expenses/travel-plan/${travelPlanId}`),

    // 获取费用详情
    getExpenseById: (id) => request.get(`/api/expenses/${id}`),

    // 创建新费用记录
    createExpense: (data) => request.post('/api/expenses', data),

    // 更新费用记录
    updateExpense: (id, data) => request.put(`/api/expenses/${id}`, data),

    // 删除费用记录
    deleteExpense: (id) => request.delete(`/api/expenses/${id}`)
};

// AI相关API
const aiApi = {
    // AI生成行程
    generatePlan: (data) => request.post('/api/ai/generate-plan', data),

    // 根据文本生成行程
    generatePlanFromText: (data) => request.post('/api/ai/generate-plan-from-text', data)
};

// 语音相关API
const voiceApi = {
    // 语音转文字
    transcribeVoice: (formData) => request.post('/api/voice/transcribe', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        timeout: 30000
    })
};

// 导出所有API
export {
    authApi,
    travelPlanApi,
    expenseApi,
    aiApi,
    voiceApi
};

// 工具函数：检查用户是否已登录
export const checkLogin = () => {
    return !!localStorage.getItem('token');
};

// 工具函数：获取当前登录用户
export const getCurrentUser = () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
};

// 工具函数：保存登录信息
export const saveLoginInfo = (token, user) => {
    // 保存到localStorage
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
};

// 工具函数：清除登录信息
export const clearLoginInfo = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
};


export const redirectWithToken = (url) => {
    // 创建XMLHttpRequest请求携带token获取页面
    const token = localStorage.getItem('token')
    if (!token) {
        ElementPlus.ElMessage.error('登录过期，请重新登录');
        // 清除过期token
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        // 跳转到登录页
        window.location.href = 'login.html';
        return;
    }
    const xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.setRequestHeader('Authorization', `Bearer ${token}`);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            // 请求成功，跳转到页面
            window.location.href = url;
        } else if (xhr.readyState === 4) {
            // 请求失败，显示错误信息
            ElementPlus.ElMessage.error('页面跳转失败，请手动访问行程管理页面');
        }
    };
    xhr.send();
}