package me.hl.webfluxapi.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("ITEM")
data class Item (
    @Id
    @Column("ID_ITEM")
    val id: Long?,
    @Column("TXT_NAME")
    var name: String
)