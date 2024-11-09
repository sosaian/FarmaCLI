# FarmaCLI

CILSA | Curso JAVA | Proyecto final

## Información del proyecto

Este fue el proyecto final del curso, donde pudimos poner en práctica los conocimientos que aprendimos así como también los que ya teníamos previamente, en un grupo donde pudimos compartir ideas para encontrar una propuesta con la que cumplimos los requerimientos del proyecto.

### Descripción

FarmaCLI es una aplicación CLI que busca poder extraer información del [Listado de precios de medicamentos para los laboratorios, farmacias, droguerías y otras entidades](https://datos.gob.ar/dataset/pami-listado-precios-medicamentos-para-entidades/archivo/pami_59b035cf-09b3-4058-b06b-8e49b99fa698), provistos públicamente de [datos.gob.ar](https://datos.gob.ar/). La idea es que uno pueda conseguir la versión actualizada directamente de ese link (el archivo en formato `.csv`) y con esta aplicación lograr hacer búsquedas con el fin de extraer la información respecto de un medicamento en particular (precios, compuestos, etc.).

## Miembros del grupo

* Yesica Anali Sofra ([@yesica1983](https://github.com/yesica1983))
* Ian Sosa ([@sosaian](https://github.com/sosaian))

## Instalación del repositorio de manera local

Requerimientos previos para poder hacer uso de FarmaCLI:
* `Java 17 SDK`
* `Variables de entorno del sistema` (que se necesitan configurar antes de hacer uso de la misma)
* IDE: `Jetbrains Intellij IDEA`, `Eclipse` o `Visual Studio Code` (con pack de extensiones de JAVA)

<details>
  <summary>Instalación usando git clone 🔧</summary>

### Cómo clonar el proyecto

Sigue estos pasos para clonar el repositorio e instalar las dependencias necesarias:

1. **Clonar el repositorio**
   Ejecuta el siguiente comando en tu terminal, reemplazando `URL_DEL_PROYECTO` por el enlace del proyecto:

```sh
git clone URL_DEL_PROYECTO
```
</details>

<details>
  <summary>Instalación descargando comprimido ZIP 🔧</summary>

### Cómo descargar el proyecto

Sigue estos pasos para clonar el repositorio e instalar las dependencias necesarias:

1. **Descargar el proyecto en formato ZIP**
   Ir a “code” > download ZIP

2. **Descomprimir el archivo**
</details>

<details>
   <summary>Agregar variables de entorno en Windows 10</summary>

> [!NOTE]
> Se asume que llegado a este punto ya se dispone de un archivo .csv en el dispositivo para usar con FarmaCLI. Si no es el caso, ir a la sección `Descripción` en el [README.md](https://github.com/sosaian/FarmaCLI) para acceder al link del archivo ejemplo en el que se inspiró este proyecto.


Para agregar variables de entorno en Windows 10, sigue estos pasos:

1. **Acceder a la configuración avanzada del sistema**:

   * Hacer clic en el botón **Inicio** y escribir **“Sistema”** en la barra de búsqueda.

   * Seleccionar **“Sistema”** en el menú desplegable.

   * Hacer clic en **“Configuración avanzada del sistema”** en la ventana que se abre.

2. **Editar variables de entorno**:

   * En la ventana **“Configuración avanzada del sistema”**, hacer clic en **“Variables de entorno”** en la sección **“Sistema”**.

     > TIP: En la ventana **“Variables de entorno”**, se pueden editar, eliminar o agregar nuevas variables.

   * Para agregar las variables de entorno que necesita FarmaCLI, hacer clic en **“Nuevo”** y rellenar los campos con la siguiente información:

       ```
       Nombre: FARMACLI_CSV_PATH
       Valor: <RUTA APUNTANDO AL ARCHIVO .CSV>
       ```

   * Repetir esos mismos pasos para crear esta otra variable:

       ```
       Nombre: FARMACLI_RESULT_PATH
       Valor: <RUTA DONDE SE GUARDARÁN LOS RESULTADOS>
       ```

   * Hacer clic en **“Aceptar”** para guardar la nueva variable.
</details>

Por último, abrir el proyecto con el IDE elegido, y ejecutando `Main.java` ya se puede hacer uso de FarmaCLI!

### Cómo importar el proyecto en Eclipse

<details>
   <summary>(Opcional) Crear nuevo espacio de trabajo en Eclipse</summary>
   
   Para crear un nuevo espacio de trabajo en Eclipse, sigue estos pasos:
   
   1. `Archivo > Cambiar espacio de trabajo > Otro…` (o presiona Ctrl + Shift + E en Windows/Linux o Cmd + Shift + E en Mac)
   2. Selecciona la ubicación donde deseas crear el nuevo espacio de trabajo y escribe un nombre para él.
   3. Haz clic en Crear para crear el nuevo espacio de trabajo.
   
   Alternativamente, puedes crear un nuevo espacio de trabajo al iniciar Eclipse:
   
   1. Al abrir Eclipse, se te pedirá que elijas un espacio de trabajo. Haz clic en Crear y sigue los pasos mencionados anteriormente.
</details>

#### Crear proyecto en Eclipse

1. Al abrir Eclipse, ir a **File** -> **New** -> **Java Project**
2. Asignar un nombre al proyecto en **Project Name** (e.g. `FarmaCLI`)
3. Quitar check en **Use default location** y asignar la ruta del directorio donde instalamos el repositorio.
4. Una vez verificada toda la información crear el proyecto en **Finish**

¡Ejecutando `Main.java` ya se puede hacer uso de FarmaCLI!

#### Recomendaciones
* Es recomendable crear un nuevo espacio de trabajo para cada proyecto o grupo de proyectos relacionados, para mantener organizado el contenido y evitar confusiones.
* Si tienes un proyecto que se ha corrompido o tienes problemas al abrirlo, crear un nuevo espacio de trabajo e importar el proyecto allí puede ser una buena solución.