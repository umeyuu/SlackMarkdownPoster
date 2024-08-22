package org.example

import java.io.File
import java.util.Properties

import org.yaml.snakeyaml.Yaml


object ConfigLoader {
    private val properties = Properties()

    // YAMLファイルをロードしてプロパティに変換
    fun loadYml(filePath: String) {
        val yaml = Yaml()
        val inputStream = File(filePath).inputStream()
        val yamlMap: Map<String, Any> = yaml.load(inputStream)
        flattenMap(yamlMap, "")
    }

    // ネストされたマップをフラット化してプロパティに格納
    private fun flattenMap(map: Map<String, Any>, parentKey: String) {
        for ((key, value) in map) {
            val fullKey = if (parentKey.isEmpty()) key else "$parentKey.$key"
            when (value) {
                is Map<*, *> -> flattenMap(value as Map<String, Any>, fullKey)
                else -> properties[fullKey] = value.toString()
            }
        }
    }

    // プロパティ値を取得
    fun getProperty(key: String): String? {
        return properties.getProperty(key)
    }
}
