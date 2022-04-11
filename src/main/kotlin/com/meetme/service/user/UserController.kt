package com.meetme.service.user

import com.meetme.domain.dto.DataResponse
import com.meetme.domain.dto.auth.LoginCredentialsDto
import com.meetme.domain.dto.auth.RegisterCredentialsDto
import com.meetme.domain.dto.auth.UserDto
import com.meetme.domain.dto.user.EditUserDto
import com.meetme.util.tryExecute
import com.meetme.db.user.User
import com.meetme.service.user.mapper.UserToUserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class UserController {

    @Autowired
    private lateinit var userService: UserServiceImpl

    @Autowired
    private lateinit var userToUserDto: UserToUserDto

    @PostMapping("/register")
    fun register(@RequestBody credentials: RegisterCredentialsDto): DataResponse<UserDto> =
        tryExecute {
            val user = userService.create(credentials)
            userToUserDto(user)
        }

    @PostMapping("/login")
    fun login(@RequestBody credentials: LoginCredentialsDto): DataResponse<UserDto> =
        tryExecute {
            val user = userService.loginUser(
                email = credentials.email,
                password = credentials.password,
            )
            userToUserDto(user)
        }

    @GetMapping("/{user_id}")
    fun getUser(@PathVariable("user_id") userId: Long): DataResponse<User> =
        tryExecute {
            userService.get(userId)
        }

    @PostMapping("{user_id}/edit")
    fun editUser(
        @PathVariable("user_id") userId: Long,
        @RequestBody editUserDto: EditUserDto,
    ): DataResponse<UserDto> =
        tryExecute {
            userToUserDto(userService.update(userId, editUserDto))
        }
}
