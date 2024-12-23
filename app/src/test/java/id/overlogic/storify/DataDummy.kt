package id.overlogic.storify

import id.overlogic.storify.data.source.local.entity.StoryEntity

object DataDummy {

    fun generateDummyQuoteResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryEntity(
                id = i.toString(),
                name = "name + $i",
                description = "description $i",
                photoUrl = "https://picsum.photos/id/$i/200",
                createdAt = "2020-01-01T12:00:00Z",
                lat = 10.0 + i,
                lon = 100.0 + i,
            )
            items.add(quote)
        }
        return items
    }
}