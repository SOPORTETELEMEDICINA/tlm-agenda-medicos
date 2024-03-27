# nio-agenda-medicos

Este proyecto tiene 2 conexiones hacía las bases de datos nio-agenda-medicos y nio-eventos.

Nio-eventos es solo una tabla de lectura que tiene réplica de la tabla de eventos en la base de datos nio-expediente.


nio-agenda-médicos contiene otra tabla eventos en la que se guardan los registros que se hagan cuando el usuario no 
esté registrado, estos registros nuevos se replican a nio-expediente y esta tabla los replica a nio-eventos
