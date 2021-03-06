package org.destiny.cloud.microserviceprovideruser.dao;

import org.destiny.cloud.microserviceprovideruser.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 王康
 * hzwangkang1@corp.netease.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * Corpright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * @version JDK 1.8.0_101
 * @since 2018/7/29 19:40
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
