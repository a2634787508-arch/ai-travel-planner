-- 创建数据库和用户
CREATE DATABASE IF NOT EXISTS travel_ai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE travel_ai;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    avatar VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- 行程表
CREATE TABLE IF NOT EXISTS itineraries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    destination VARCHAR(200) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    budget DECIMAL(10,2),
    travelers_count INT DEFAULT 1,
    status ENUM('planning', 'confirmed', 'completed', 'cancelled') DEFAULT 'planning',
    ai_suggestions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_destination (destination),
    INDEX idx_dates (start_date, end_date)
);

-- 行程详情表
CREATE TABLE IF NOT EXISTS itinerary_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    itinerary_id BIGINT NOT NULL,
    day_number INT NOT NULL,
    date DATE NOT NULL,
    time_slot VARCHAR(50),
    activity VARCHAR(200) NOT NULL,
    location VARCHAR(200),
    description TEXT,
    estimated_cost DECIMAL(8,2),
    transportation VARCHAR(100),
    accommodation VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (itinerary_id) REFERENCES itineraries(id) ON DELETE CASCADE,
    INDEX idx_itinerary_id (itinerary_id),
    INDEX idx_date (date),
    INDEX idx_day_number (day_number)
);

-- 景点表
CREATE TABLE IF NOT EXISTS attractions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    province VARCHAR(100),
    country VARCHAR(100) DEFAULT '中国',
    category VARCHAR(50),
    description TEXT,
    rating DECIMAL(3,2) DEFAULT 0.00,
    ticket_price DECIMAL(8,2),
    opening_hours VARCHAR(100),
    address VARCHAR(300),
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    images TEXT,
    tags VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_city (city),
    INDEX idx_category (category),
    INDEX idx_rating (rating),
    FULLTEXT idx_name_desc (name, description)
);

-- 用户收藏表
CREATE TABLE IF NOT EXISTS user_favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    attraction_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (attraction_id) REFERENCES attractions(id) ON DELETE CASCADE,
    UNIQUE KEY unique_favorite (user_id, attraction_id),
    INDEX idx_user_id (user_id),
    INDEX idx_attraction_id (attraction_id)
);

-- 插入一些示例数据
INSERT INTO users (username, password, email, phone) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVjzieMwkOmANgNOgKQNNBDvAGK', 'admin@travelai.com', '13800138000'),
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVjzieMwkOmANgNOgKQNNBDvAGK', 'test@example.com', '13900139000');

INSERT INTO attractions (name, city, province, category, description, rating, ticket_price, opening_hours, address) VALUES 
('故宫博物院', '北京', '北京', '历史文化', '中国明清两代的皇家宫殿，世界上现存规模最大、保存最为完整的木质结构古建筑之一。', 4.8, 60.00, '08:30-17:00', '北京市东城区景山前街4号'),
('西湖', '杭州', '浙江', '自然风光', '杭州著名的风景名胜区，以其秀丽的湖光山色和众多的名胜古迹而闻名中外。', 4.7, 0.00, '全天开放', '浙江省杭州市西湖区'),
('外滩', '上海', '上海', '城市地标', '位于上海市黄浦区的黄浦江畔，是最具上海城市象征意义的景点之一。', 4.6, 0.00, '全天开放', '上海市黄浦区中山东一路'),
('兵马俑', '西安', '陕西', '历史文化', '秦始皇陵兵马俑，世界第八大奇迹，是中国古代辉煌文明的一张金字名片。', 4.9, 120.00, '08:30-18:00', '陕西省西安市临潼区秦陵北路'),
('张家界国家森林公园', '张家界', '湖南', '自然风光', '中国第一个国家森林公园，以峰称奇、以谷显幽、以林见秀。', 4.7, 225.00, '07:00-18:00', '湖南省张家界市武陵源区');

-- 创建视图：行程统计
CREATE VIEW IF NOT EXISTS itinerary_stats AS
SELECT 
    u.id as user_id,
    u.username,
    COUNT(i.id) as total_itineraries,
    SUM(CASE WHEN i.status = 'completed' THEN 1 ELSE 0 END) as completed_itineraries,
    SUM(CASE WHEN i.status = 'planning' THEN 1 ELSE 0 END) as planning_itineraries,
    SUM(i.budget) as total_budget
FROM users u
LEFT JOIN itineraries i ON u.id = i.user_id
GROUP BY u.id, u.username;