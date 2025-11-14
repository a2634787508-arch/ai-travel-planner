# 智能旅游规划系统 RESTful API 设计文档

## 1. 用户相关接口

### 1.1 用户注册
- **URL**: `POST /api/auth/register`
- **描述**: 用户注册接口
- **请求体**:
```json
{
  "username": "string",  // 用户名
  "password": "string",  // 密码
  "email": "string"      // 邮箱（可选）
}
```
- **成功响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "username": "string",
    "email": "string"
  }
}
```
- **失败响应**:
```json
{
  "code": 400,
  "message": "用户名已存在",
  "data": null
}
```

### 1.2 用户登录
- **URL**: `POST /api/auth/login`
- **描述**: 用户登录接口
- **请求体**:
```json
{
  "username": "string",  // 用户名
  "password": "string"   // 密码
}
```
- **成功响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "string",    // JWT token
    "user": {
      "id": 1,
      "username": "string",
      "email": "string"
    }
  }
}
```
- **失败响应**:
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

### 1.3 用户退出
- **URL**: `POST /api/auth/logout`
- **描述**: 用户退出登录
- **请求头**:
  - `Authorization: Bearer {token}`
- **成功响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

## 2. 行程相关接口

### 2.1 获取用户所有行程
- **URL**: `GET /api/travel-plans`
- **描述**: 获取当前登录用户的所有行程列表
- **请求头**:
  - `Authorization: Bearer {token}`
- **成功响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "destination": "string",
      "startDate": "2024-01-01",
      "endDate": "2024-01-05",
      "totalBudget": 10000,
      "peopleCount": 2,
      "preferences": "string",
      "createdTime": "2024-01-01T00:00:00"
    }
  ]
}
```

### 2.2 创建新行程
- **URL**: `POST /api/travel-plans`
- **描述**: 创建新的行程计划
- **请求头**:
  - `Authorization: Bearer {token}`
- **请求体**:
```json
{
  "destination": "string",
  "startDate": "2024-01-01",
  "endDate": "2024-01-05",
  "totalBudget": 10000,
  "peopleCount": 2,
  "preferences": "string",
  "generatedItinerary": "string"  // AI生成的行程内容
}
```
- **成功响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "destination": "string",
    "startDate": "2024-01-01",
    "endDate": "2024-01-05",
    "totalBudget": 10000,
    "peopleCount": 2,
    "preferences": "string",
    "generatedItinerary": "string",
    "createdTime": "2024-01-01T00:00:00"
  }
}
```

### 2.3 获取行程详情
- **URL**: `GET /api/travel-plans/{id}`
- **描述**: 获取指定行程的详细信息
- **请求头**:
  - `Authorization: Bearer {token}`
- **成功响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "destination": "string",
    "startDate": "2024-01-01",
    "endDate": "2024-01-05",
    "totalBudget": 10000,
    "peopleCount": 2,
    "preferences": "string",
    "generatedItinerary": "string",
    "createdTime": "2024-01-01T00:00:00",
    "expenses": [
      {
        "id": 1,
        "category": "string",
        "itemName": "string",
        "amount": 500,
        "note": "string",
        "createdTime": "2024-01-01T00:00:00"
      }
    ]
  }
}
```

### 2.4 更新行程
- **URL**: `PUT /api/travel-plans/{id}`
- **描述**: 更新行程信息
- **请求头**:
  - `Authorization: Bearer {token}`
- **请求体**:
```json
{
  "destination": "string",
  "startDate": "2024-01-01",
  "endDate": "2024-01-05",
  "totalBudget": 10000,
  "peopleCount": 2,
  "preferences": "string",
  "generatedItinerary": "string"
}
```
- **成功响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "destination": "string",
    "startDate": "2024-01-01",
    "endDate": "2024-01-05",
    "totalBudget": 10000,
    "peopleCount": 2,
    "preferences": "string",
    "generatedItinerary": "string",
    "createdTime": "2024-01-01T00:00:00"
  }
}
```

### 2.5 删除行程
- **URL**: `DELETE /api/travel-plans/{id}`
- **描述**: 删除指定行程
- **请求头**:
  - `Authorization: Bearer {token}`
- **成功响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

## 3. 费用管理接口

### 3.1 添加费用记录
- **URL**: `POST /api/travel-plans/{planId}/expenses`
- **描述**: 为指定行程添加费用记录
- **请求头**:
  - `Authorization: Bearer {token}`
- **请求体**:
```json
{
  "category": "string",  // 交通、住宿、餐饮等
  "itemName": "string",
  "amount": 500,
  "note": "string"
}
```
- **成功响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "category": "string",
    "itemName": "string",
    "amount": 500,
    "note": "string",
    "createdTime": "2024-01-01T00:00:00"
  }
}
```

### 3.2 删除费用记录
- **URL**: `DELETE /api/travel-plans/{planId}/expenses/{expenseId}`
- **描述**: 删除指定的费用记录
- **请求头**:
  - `Authorization: Bearer {token}`
- **成功响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

## 4. 工具接口

### 4.1 语音转文字
- **URL**: `POST /api/voice/transcribe`
- **描述**: 语音文件转文字
- **请求头**:
  - `Authorization: Bearer {token}`
- **请求体**:
  - FormData: `file` (音频文件)
- **成功响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "text": "string"  // 转换后的文本内容
  }
}
```

### 4.2 AI生成行程
- **URL**: `POST /api/ai/generate-plan`
- **描述**: 通过AI生成旅行计划
- **请求头**:
  - `Authorization: Bearer {token}`
- **请求体**:
```json
{
  "destination": "string",
  "startDate": "2024-01-01",
  "endDate": "2024-01-05",
  "totalBudget": 10000,
  "peopleCount": 2,
  "preferences": "string",
  "textInput": "string"  // 可选，用户的文本描述
}
```
- **成功响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "generatedItinerary": "string"  // AI生成的完整行程内容
  }
}
```

## 5. 通用响应格式

所有API接口返回统一格式：
```json
{
  "code": 0,          // 状态码：0成功，非0失败
  "message": "success",  // 提示信息
  "data": {}          // 数据内容，根据接口返回不同数据
}
```

## 6. 错误码定义

| 错误码 | 描述 |
|--------|------|
| 0 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/认证失败 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 5001 | 用户名已存在 |
| 5002 | 用户名或密码错误 |
| 5003 | 语音识别失败 |
| 5004 | AI生成失败 |