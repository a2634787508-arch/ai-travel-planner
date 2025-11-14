#!/bin/bash

# 旅游规划AI系统 Docker 构建脚本
# 作者: Travel AI Team
# 版本: 1.0

set -e

echo "🚀 开始构建旅游规划AI系统 Docker 镜像..."

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker 未安装，请先安装 Docker${NC}"
    exit 1
fi

# 检查docker-compose是否安装
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ docker-compose 未安装，请先安装 docker-compose${NC}"
    exit 1
fi

# 创建必要的目录
echo -e "${BLUE}📁 创建必要的目录...${NC}"
mkdir -p logs
mkdir -p uploads
mkdir -p docker/mysql

# 检查环境变量文件
if [ ! -f .env ]; then
    echo -e "${YELLOW}⚠️  未找到 .env 文件，创建示例文件...${NC}"
    cat > .env << EOF
# 阿里云百炼API密钥 (请替换为您的实际密钥)
DASHSCOPE_API_KEY=your_dashscope_api_key_here

# 数据库配置
MYSQL_ROOT_PASSWORD=root123456
MYSQL_DATABASE=travel_ai
MYSQL_USER=travel_user
MYSQL_PASSWORD=travel_pass

# 应用配置
SPRING_PROFILES_ACTIVE=docker
JWT_SECRET=travelAiSecretKey2024ForJwtTokenGeneration
EOF
    echo -e "${YELLOW}⚠️  请编辑 .env 文件，填入您的实际配置${NC}"
fi

# 构建镜像
echo -e "${BLUE}🔨 构建 Docker 镜像...${NC}"
docker build -t travel-ai-planner:latest .

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Docker 镜像构建成功！${NC}"
else
    echo -e "${RED}❌ Docker 镜像构建失败！${NC}"
    exit 1
fi

# 询问是否启动服务
echo -e "${YELLOW}🤔 是否要启动服务？(y/n)${NC}"
read -r response
if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
    echo -e "${BLUE}🚀 启动服务...${NC}"
    docker-compose up -d
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ 服务启动成功！${NC}"
        echo -e "${GREEN}📱 应用访问地址: http://localhost:8080${NC}"
        echo -e "${GREEN}🗄️  数据库连接: localhost:3306${NC}"
        echo -e "${BLUE}📊 查看服务状态: docker-compose ps${NC}"
        echo -e "${BLUE}📝 查看日志: docker-compose logs -f${NC}"
        echo -e "${BLUE}🛑 停止服务: docker-compose down${NC}"
    else
        echo -e "${RED}❌ 服务启动失败！${NC}"
        exit 1
    fi
fi

echo -e "${GREEN}🎉 构建完成！${NC}"