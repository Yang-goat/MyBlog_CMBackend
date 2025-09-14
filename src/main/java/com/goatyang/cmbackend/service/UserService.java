package com.goatyang.cmbackend.service;

import com.goatyang.cmbackend.model.User;
import com.goatyang.cmbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务类
 * 处理用户相关的业务逻辑，作为控制器与数据访问层之间的中间层
 * 提供用户的创建、查询、更新、删除等核心功能
 */
@Service
public class UserService {

    // 注入用户数据访问层对象，用于数据库操作
    private final UserRepository userRepository;

    /**
     * 构造方法，通过@Autowired实现依赖注入
     * @param userRepository 用户数据访问层对象
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 创建新用户
     * 包含业务校验：检查GitHub ID是否已被注册
     * @param user 待创建的用户对象
     * @return 创建成功的用户对象（包含自动生成的ID等信息）
     * @throws IllegalArgumentException 当GitHub ID已被注册时抛出
     */
    @Transactional  // 标记为事务方法，确保数据操作的原子性
    public User createUser(User user) {
        // 检查GitHub ID是否已存在，避免重复注册
        if (userRepository.existsByGithubId(user.getGithubId())) {
            throw new IllegalArgumentException("GitHub ID已被注册");
        }
        // 保存用户信息到数据库
        return userRepository.save(user);
    }

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 包含用户信息的Optional对象，若不存在则为empty
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 根据GitHub ID查询用户
     * 用于关联第三方账号与本地用户
     * @param githubId GitHub账号ID
     * @return 包含用户信息的Optional对象，若不存在则为empty
     */
    public Optional<User> getUserByGithubId(Long githubId) {
        return userRepository.findByGithubId(githubId);
    }

    /**
     * 查询所有用户
     * 通常用于管理员查看用户列表
     * @return 所有用户的列表集合
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 更新用户信息
     * 仅更新指定ID的用户的部分字段
     * @param id 要更新的用户ID
     * @param userDetails 包含新用户信息的对象
     * @return 更新后的用户对象
     * @throws IllegalArgumentException 当用户不存在时抛出
     */
    @Transactional  // 标记为事务方法
    public User updateUser(Long id, User userDetails) {
        // 查找指定ID的用户，若存在则更新信息，否则抛出异常
        return userRepository.findById(id)
                .map(user -> {
                    // 更新用户信息（仅更新部分关键字段）
                    user.setUsername(userDetails.getUsername());
                    user.setAvatarUrl(userDetails.getAvatarUrl());
                    user.setProfileUrl(userDetails.getProfileUrl());
                    user.setEmail(userDetails.getEmail());
                    user.setAccessToken(userDetails.getAccessToken());
                    // 保存更新后的用户信息
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("用户不存在，ID: " + id));
    }

    /**
     * 删除用户
     * 先检查用户是否存在，再执行删除操作
     * @param id 要删除的用户ID
     * @throws IllegalArgumentException 当用户不存在时抛出
     */
    @Transactional  // 标记为事务方法
    public void deleteUser(Long id) {
        // 检查用户是否存在
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("用户不存在，ID: " + id);
        }
        // 执行删除操作
        userRepository.deleteById(id);
    }
}