import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;


public class run {

    //метод, определяющий, возможно ли разместить всех гостей
    public static boolean checkCapacity(int maxCapacity, List<Map<String, String>> guests) {
        Map<String, Integer> dates = new HashMap<>();
        for (Map<String, String> guest : guests) {
            String checkIn = guest.get("check-in");
            String checkOut = guest.get("check-out");
            dates.put(checkIn, dates.getOrDefault(checkIn, 0) + 1);
            dates.put(checkOut, dates.getOrDefault(checkOut, 0) - 1);
        }

        List<String> sortedDates = new ArrayList<>(dates.keySet());
        Collections.sort(sortedDates);

        int numberOfGuests = 0;
        for (String date : sortedDates) {
            numberOfGuests += dates.get(date);
            if (numberOfGuests > maxCapacity) {
                return false;
            }
        }
        return true;
    }


    // Вспомогательный метод для парсинга JSON строки в Map
    private static Map<String, String> parseJsonToMap(String json) {
        Map<String, String> map = new HashMap<>();
        // Удаляем фигурные скобки
        json = json.substring(1, json.length() - 1);


        // Разбиваем на пары ключ-значение
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");
            map.put(key, value);
        }

        return map;
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        // Первая строка - вместимость гостиницы
        int maxCapacity = Integer.parseInt(scanner.nextLine());


        // Вторая строка - количество записей о гостях
        int n = Integer.parseInt(scanner.nextLine());


        List<Map<String, String>> guests = new ArrayList<>();


        // Читаем n строк, json-данные о посещении
        for (int i = 0; i < n; i++) {
            String jsonGuest = scanner.nextLine();
            // Простой парсер JSON строки в Map
            Map<String, String> guest = parseJsonToMap(jsonGuest);
            guests.add(guest);
        }


        // Вызов функции
        boolean result = checkCapacity(maxCapacity, guests);


        // Вывод результата
        System.out.println(result ? "True" : "False");


        scanner.close();
    }
}