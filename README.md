[Download | Скачать](https://modrinth.com/plugin/sizeplayer)

# EN

The plugin allows you to change a player's size using convenient commands. Size directly affects the maximum health, which is calculated using configurable formulas.

## Commands

- ```/sizeplayer set <size> <nickname>``` — change your own size or another player's size.
- ```/sizeplayer reload``` — reload the plugin configuration.

## Health

When the size is changed, the player's maximum health is automatically adjusted.

Examples (default):

- Size 0.4 → ~4 hearts.
- Size 1 → 10 hearts.
- Size 10 → ~20 hearts.

The formulas are fully configurable in the config, allowing flexible gameplay balancing.

## Formulas
Mathematical expressions are supported (via exp4j). The variable ```x``` is used — the player's size.

Separate formulas for:
- small players (x < 1).
- large players (x > 1).

## Configuration
- HEX color support (&#RRGGBB).
- Fully customizable messages.
- Russian and English languages are supported. You can create your own localization file and specify its name in ```config.yml```.
- Configuration changes can be applied without restarting the server by using ```/sizeplayer reload```.

## Size Change Animation

The plugin supports smooth player size transitions with configurable animation parameters.

The following settings can be configured in ```config.yml```:

- Update interval — how often the player's size is updated during the animation, for example, every 1 tick.
- Change step — how much the player's size changes per interval, for example, 0.1.
- Enable or disable animation — choose between gradual and instant size changes.

## Permissions
- ```sizeplayer.set``` — change your own size.
- ```sizeplayer.set.names``` — change other players' sizes.
- ```sizeplayer.reload``` — reload the plugin configuration.

# RU

Плагин позволяет изменять размер игрока с помощью удобных команд. Размер напрямую влияет на максимальное здоровье, которое рассчитывается по настраиваемым формулам.

## Команды

- ```/sizeplayer set <размер> <ник>``` — изменить свой размер или размер другого игрока;
- ```/sizeplayer reload``` — перезагрузить конфигурацию плагина.

## Здоровье

При изменении размера автоматически изменяется максимальное здоровье игрока.

Примеры (по умолчанию):

- размер 0.4 → ~4 сердца;
- размер 1 → 10 сердец;
- размер 10 → ~20 сердец.

Формулы полностью настраиваются в конфиге, что позволяет гибко балансировать геймплей.

## Формулы

Поддерживаются математические выражения (через exp4j). Используется переменная ```x``` — размер игрока.

Отдельные формулы для:

- маленьких игроков (x < 1);
- больших игроков (x > 1).

## Настройка

- поддержка HEX-цветов (&#RRGGBB);
- полностью настраиваемые сообщения;
- доступны русский и английский языки. При желании можно создать собственный файл локализации и указать его название в ```config.yml```.
- применение изменений конфигурации без необходимости перезапускать сервер, используя ```/sizeplayer reload```.

## Анимация изменения размера

Плагин поддерживает плавное изменение размера игрока с настраиваемыми параметрами анимации.

В ```config.yml``` можно настроить:

- интервал изменения — частоту обновления размера, например каждые 1 тик;
- шаг изменения — величину изменения размера за один интервал, например 0.1;
- включение и отключение анимации — возможность использовать мгновенное изменение размера вместо постепенного.

## Права
- ```sizeplayer.set``` — изменение собственного размера;
- ```sizeplayer.set.names``` — изменение размера других игроков;
- ```sizeplayer.reload``` — перезагрузка конфигурации плагина.
