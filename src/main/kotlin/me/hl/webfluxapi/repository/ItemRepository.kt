package me.hl.webfluxapi.repository

import me.hl.webfluxapi.domain.Item
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ItemRepository: ReactiveCrudRepository<Item, Long>