package com.example.flashcardbackend.utils

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundException(s: String = "Data not found") : RuntimeException(s)

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class BadRequestException(s: String = "Bad request") : RuntimeException(s)
