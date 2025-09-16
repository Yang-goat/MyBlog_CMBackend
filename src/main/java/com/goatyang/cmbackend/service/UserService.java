package com.goatyang.cmbackend.service;

import com.goatyang.cmbackend.model.User;
import com.goatyang.cmbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务层
 * 封装用户相关的业务逻辑
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** 查询所有用户 */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** 根据ID查询用户 */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /** 根据GitHub ID查询用户 */
    public Optional<User> getUserByGithubId(Long githubId) {
        return userRepository.findByGithubId(githubId);
    }

    /** 根据邮箱查询用户 */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /** 根据用户名查询用户 */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /** 更新用户评论权限 */
    @Transactional
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setComPermissions(userDetails.getComPermissions());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("用户不存在，ID: " + id));
    }

    /** 删除用户 */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("用户不存在，ID: " + id);
        }
        userRepository.deleteById(id);
    }
}
