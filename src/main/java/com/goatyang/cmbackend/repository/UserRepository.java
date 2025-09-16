package com.goatyang.cmbackend.repository;

import com.goatyang.cmbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层接口
 * 用于与数据库进行交互，处理用户相关的CRUD操作
 */
@Repository
// 标注该接口为数据访问层组件，由Spring进行管理
// 继承JpaRepository<User, Long>，获得基本的CRUD操作能力
// 其中User是实体类类型，Long是主键(id)的数据类型
public interface UserRepository extends JpaRepository<User, Long> {

    // 根据GitHub ID查询用户（用于GitHub登录时匹配本地用户）
    Optional<User> findByGithubId(Long githubId);

    // 根据用户名查询用户（用于评论展示、用户资料查询）
    Optional<User> findByUsername(String username);

    // 根据邮箱查询用户（用于账号验证或通知功能）
    Optional<User> findByEmail(String email);

    // 检查GitHub ID是否已存在（用于避免重复创建用户）
    boolean existsByGithubId(Long githubId);

    // 检查用户名是否已存在（用于用户名唯一性验证）
//    boolean existsByUsername(String username);

    // 检查邮箱是否已存在（用于邮箱唯一性验证）
//    boolean existsByEmail(String email);
}
