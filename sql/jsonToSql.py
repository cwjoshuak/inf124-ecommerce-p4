import json
import pymysql.cursors

connection = pymysql.connect(host='127.0.0.1',
                             user='root',
                             password='inf124',
                             db='ecrocs',
                             charset='utf8mb4',
                             cursorclass=pymysql.cursors.DictCursor)

with open("shoes.json") as shoe_json:
    with connection.cursor() as cursor:
        shoes = json.load(shoe_json)
        shoes = shoes["shoes"]
        for shoe in shoes:
            sql = "INSERT INTO `shoes` (`type`, `id`, `name`, `desc1`, `desc2`, `price`) VALUES (%s, %s, %s, %s, %s, %s)"
            cursor.execute(sql, (shoe["type"], shoe["id"], shoe["name"], shoe["desc1"], shoe["desc2"], shoe["price"]))
            for index, (cname, colors) in enumerate(shoe['colors'].items()):
                sql = "INSERT INTO `shoe_colors` (`shoe_id`, `color_name`, `color_hex`, `file_name`) VALUES (%s, %s, %s, %s)"
                for color in colors:
                    cursor.execute(sql, (shoe["id"], cname, color, f"product_{index}"))
            for size in shoe['sizes']:
                sql = "INSERT INTO `shoe_sizes` (`shoe_id`, `size`) VALUES (%s, %s)"
                cursor.execute(sql, (shoe["id"], size))

            for index, detail in enumerate(shoe['details'],1):
                sql = "INSERT INTO `shoe_details` (`id`, `shoe_id`, `details`) VALUES (%s, %s, %s)"
                cursor.execute(sql, (index, shoe["id"], detail))

        connection.commit()