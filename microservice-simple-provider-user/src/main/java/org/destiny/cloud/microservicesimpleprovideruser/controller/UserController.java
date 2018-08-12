package org.destiny.cloud.microservicesimpleprovideruser.controller;

import org.destiny.cloud.microservicesimpleprovideruser.dao.UserRepository;
import org.destiny.cloud.microservicesimpleprovideruser.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 王康
 * hzwangkang1@corp.netease.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * Corpright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * @version JDK 1.8.0_101
 * @since 2018/7/29 19:41
 */
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/simple/{id}")
    public User findById(@PathVariable("id") Long id) {
        return userRepository.getOne(id);
    }

}
