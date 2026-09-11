## OpticalJS 2.0.0

Updated to Neoforge 1.21.1

Example usage:
```js
ServerEvents.recipes(event => {
    event.recipes.create_optical.focusing(
        // at least one output, multiple outputs allowed
        ["ae2:terminal", "ae2:cable_anchor"],
        ['#ae2:illuminated_panel', "create:rose_quartz"],
        123,
        'gamma'
    )

    event.recipes.create_optical.focusing(
        // you can also specify output chance for outputs
        ["ae2:terminal", {id: "ae2:cable_anchor", chance: 0.1}],
        ['#ae2:illuminated_panel', "create:rose_quartz"]
        // processing time and mode is optional
        //123,
        //'gamma'
    )
})
```

Note that currently Create Optical (0.4.2) seems unable to handle beam type properly, causing `mode` to b doing nothing. The same applies to builtin Class Pane -> Mirror recipe