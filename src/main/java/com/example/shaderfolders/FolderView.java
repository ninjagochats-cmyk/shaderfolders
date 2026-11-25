package com.example.shaderfolders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Состояние «проводника» по папкам в меню шейдеров.
 * На вход получает плоский список вида "Папка/Подпапка/Пак", на выход отдаёт
 * только содержимое текущей папки: [Назад], папки, паки.
 */
public final class FolderView {

    /** Текущая открытая папка, "" = корень shaderpacks. */
    public static String current = "";

    /** Подпись пункта списка -> путь папки, в которую он ведёт. */
    private static final Map<String, String> TARGETS = new HashMap<>();

    private FolderView() {}

    public static String targetOf(String entryName) {
        return TARGETS.get(entryName);
    }

    /** Что рисовать в списке: у паков в папках показываем только имя без пути. */
    public static String displayName(String name) {
        if (name == null || TARGETS.containsKey(name)) return name;
        int i = name.lastIndexOf('/');
        return i < 0 ? name : name.substring(i + 1);
    }

    public static List<String> build(List<String> all) {
        TARGETS.clear();

        // если открытой папки больше нет (удалили/переименовали) — в корень
        if (!current.isEmpty()) {
            final String p = current + "/";
            if (all.stream().noneMatch(n -> n.startsWith(p))) current = "";
        }

        final String prefix = current.isEmpty() ? "" : current + "/";
        Map<String, Integer> folders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        List<String> packs = new ArrayList<>();

        for (String n : all) {
            if (!n.startsWith(prefix)) continue;
            String rest = n.substring(prefix.length());
            int slash = rest.indexOf('/');
            if (slash < 0) packs.add(n);
            else folders.merge(rest.substring(0, slash), 1, Integer::sum);
        }

        List<String> out = new ArrayList<>();

        if (!current.isEmpty()) {
            int cut = current.lastIndexOf('/');
            String parent = cut < 0 ? "" : current.substring(0, cut);
            // «◂ Назад»
            String label = "\u00a77\u25c2 \u041d\u0430\u0437\u0430\u0434";
            TARGETS.put(label, parent);
            out.add(label);
        }

        for (Map.Entry<String, Integer> f : folders.entrySet()) {
            // «▸ Имя (N)»
            String label = "\u00a76\u25b8 " + f.getKey() + " \u00a78(" + f.getValue() + ")";
            TARGETS.put(label, prefix + f.getKey());
            out.add(label);
        }

        packs.sort(String.CASE_INSENSITIVE_ORDER);
        out.addAll(packs);
        return out;
    }
}
